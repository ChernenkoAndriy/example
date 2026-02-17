#!/usr/bin/env python3
"""
Remove comments from files in the repository tree rooted at 'example/'.
This script applies language-aware heuristics to avoid removing comment-like text inside string literals
and skips binary files and some directories (target, .git, .idea).

USE WITH CARE: this will overwrite files in-place. It was generated to follow the user's request to
remove all comments across the project.
"""

import sys
import os
import argparse
import io

SKIP_DIRS = {'.git', 'target', '.idea', '.mvn'}
TEXT_EXT_RULES = {
    # extension: type
    '.java': 'cstyle',
    '.kt': 'cstyle',
    '.groovy': 'cstyle',
    '.js': 'cstyle',
    '.ts': 'cstyle',
    '.c': 'cstyle',
    '.cpp': 'cstyle',
    '.h': 'cstyle',
    '.cs': 'cstyle',
    '.py': 'pyhash',
    '.sh': 'shhash',
    '.bash': 'shhash',
    'mvnw': 'shhash',
    '.yaml': 'yaml',
    '.yml': 'yaml',
    '.properties': 'props',
    '.xml': 'xml',
    '.html': 'xml',
    '.xhtml': 'xml',
    '.pom': 'xml',
    '.md': 'md',
    '.markdown': 'md',
    '.sql': 'sql',
    '.Dockerfile': 'shhash',
    'Dockerfile': 'shhash',
    '.dockerfile': 'shhash',
    '.Dockerfile': 'shhash',
    '.ini': 'props',
    '.toml': 'props',
    '.cfg': 'props',
    '.conf': 'props',
    '.gitignore': 'shhash',
    '.dockerignore': 'shhash',
    '.cmd': 'bat',
    '.ps1': 'ps1',
    '.sql': 'sql',
}

# Treat files by basename too
BASENAME_RULES = {
    'mvnw': 'shhash',
    'mvnw.cmd': 'bat',
    'Dockerfile': 'shhash',
}

def is_binary_string(bytesdata):
    # heuristic: null byte
    return b'\0' in bytesdata

# Remove C-style comments (// and /* */) but skip comment markers inside string literals
# This function works on text (str)

def remove_cstyle_comments(text):
    out = []
    i = 0
    n = len(text)
    state = 'normal'  # normal, s_quote, d_quote, line_comment, block_comment
    while i < n:
        c = text[i]
        nxt = text[i+1] if i+1 < n else ''
        if state == 'normal':
            if c == '"':
                out.append(c); state = 'd_quote'; i += 1
            elif c == "'":
                out.append(c); state = 's_quote'; i += 1
            elif c == '/' and nxt == '/':
                # start line comment: skip until newline
                i += 2
                while i < n and text[i] != '\n':
                    i += 1
                # keep the newline
            elif c == '/' and nxt == '*':
                # start block comment
                i += 2
                while i < n-1 and not (text[i] == '*' and text[i+1] == '/'):
                    i += 1
                i += 2 if i < n else 0
            else:
                out.append(c); i += 1
        elif state == 'd_quote':
            out.append(c)
            if c == '\\':
                # escape next char
                if i+1 < n:
                    out.append(text[i+1]); i += 2
                    continue
            elif c == '"':
                state = 'normal'
            i += 1
        elif state == 's_quote':
            out.append(c)
            if c == '\\':
                if i+1 < n:
                    out.append(text[i+1]); i += 2; continue
            elif c == "'":
                state = 'normal'
            i += 1
    return ''.join(out)

# Remove SQL comments: -- to end of line and /* */

def remove_sql_comments(text):
    out = []
    i = 0
    n = len(text)
    state = 'normal'  # normal, s_quote, d_quote, line_comment, block_comment
    while i < n:
        c = text[i]
        nxt = text[i+1] if i+1 < n else ''
        if state == 'normal':
            if c == '"':
                out.append(c); state = 'd_quote'; i += 1
            elif c == "'":
                out.append(c); state = 's_quote'; i += 1
            elif c == '-' and nxt == '-':
                i += 2
                while i < n and text[i] != '\n':
                    i += 1
            elif c == '/' and nxt == '*':
                i += 2
                while i < n-1 and not (text[i] == '*' and text[i+1] == '/'):
                    i += 1
                i += 2 if i < n else 0
            else:
                out.append(c); i += 1
        elif state == 'd_quote':
            out.append(c)
            if c == '\\':
                if i+1 < n:
                    out.append(text[i+1]); i += 2; continue
            elif c == '"':
                state = 'normal'
            i += 1
        elif state == 's_quote':
            out.append(c)
            if c == '\\':
                if i+1 < n:
                    out.append(text[i+1]); i += 2; continue
            elif c == "'":
                state = 'normal'
            i += 1
    return ''.join(out)

# Remove hash-style comments (#) outside of Markdown files (for yaml, sh, properties)

def remove_hash_comments(text):
    out_lines = []
    for line in text.splitlines(True):
        newline = ''
        i = 0
        n = len(line)
        in_dq = False
        in_sq = False
        while i < n:
            c = line[i]
            if c == '"' and not in_sq:
                in_dq = not in_dq; newline += c; i += 1
            elif c == "'" and not in_dq:
                in_sq = not in_sq; newline += c; i += 1
            elif c == '#' and not in_dq and not in_sq:
                # start comment: drop rest of line
                break
            else:
                newline += c; i += 1
        # keep newline char if present
        if newline.endswith('\n') or (not line.endswith('\n') and newline != ''):
            out_lines.append(newline)
        else:
            # maintain trailing newline if original had it
            if line.endswith('\n'):
                out_lines.append(newline + '\n')
            else:
                out_lines.append(newline)
    return ''.join(out_lines)

# Remove XML/HTML comments <!-- -->

def remove_xml_comments(text):
    out = []
    i = 0
    n = len(text)
    while i < n:
        if text.startswith('<!--', i):
            i += 4
            while i < n and not text.startswith('-->', i):
                i += 1
            i += 3 if i < n else 0
        else:
            out.append(text[i]); i += 1
    return ''.join(out)

# Remove Windows batch REM comments (lines starting with REM, optionally prefixed by @) and lines starting with ::

def remove_bat_comments(text):
    out_lines = []
    for line in text.splitlines(True):
        s = line.lstrip()
        if s.upper().startswith('REM ') or s.startswith('::'):
            # drop line
            continue
        # also handle @REM
        if s.upper().startswith('@REM '):
            continue
        out_lines.append(line)
    return ''.join(out_lines)

# For markdown, only remove HTML comments

def process_file(path, rule):
    try:
        with open(path, 'rb') as f:
            data = f.read()
    except Exception as e:
        print(f"skipping {path}: read error {e}")
        return False
    if is_binary_string(data):
        return False
    try:
        text = data.decode('utf-8')
    except Exception:
        try:
            text = data.decode('latin1')
        except Exception as e:
            print(f"skipping {path}: decode error {e}")
            return False
    original = text
    newtext = text
    if rule == 'cstyle':
        newtext = remove_cstyle_comments(text)
    elif rule == 'sql':
        newtext = remove_sql_comments(text)
    elif rule in ('shhash', 'yaml', 'props'):
        newtext = remove_hash_comments(text)
    elif rule == 'xml':
        newtext = remove_xml_comments(text)
    elif rule == 'md':
        newtext = remove_xml_comments(text)
    elif rule == 'bat':
        newtext = remove_bat_comments(text)
    elif rule == 'ps1':
        # Powershell uses # for comments
        newtext = remove_hash_comments(text)
    else:
        # unknown: do not change
        return False
    if newtext != original:
        # write back
        try:
            with open(path, 'w', encoding='utf-8') as f:
                f.write(newtext)
            print(f"updated: {path}")
            return True
        except Exception as e:
            print(f"failed writing {path}: {e}")
            return False
    return False


def detect_rule(path):
    basename = os.path.basename(path)
    if basename in BASENAME_RULES:
        return BASENAME_RULES[basename]
    _, ext = os.path.splitext(basename)
    if ext in TEXT_EXT_RULES:
        return TEXT_EXT_RULES[ext]
    # handle special names
    if basename.lower() == 'dockerfile':
        return 'shhash'
    # fallback: pick rules based on heuristics
    return None


def main(root):
    changed = []
    for dirpath, dirnames, filenames in os.walk(root):
        # prune
        parts = dirpath.split(os.sep)
        if any(p in SKIP_DIRS for p in parts):
            continue
        for name in filenames:
            path = os.path.join(dirpath, name)
            rule = detect_rule(path)
            if rule is None:
                # skip files without known rules to avoid accidental damage
                continue
            ok = process_file(path, rule)
            if ok:
                changed.append(path)
    print('\nSummary:')
    print(f'Files changed: {len(changed)}')
    for p in changed:
        print(p)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('root', nargs='?', default='example')
    args = parser.parse_args()
    main(args.root)
