~~~

Backcheck - a recursive file comparison tool

USAGE
  java de.backcheck.Backcheck [OPTIONS] SRC DEST

  Backcheck compares a SRC file with a DEST file.
  If SRC and DEST are both directories, Backcheck compares
  the files in these directories, recursively.
  Backcheck compares files based on file length and file contents.

OPTIONS
  -v --verbose
    Verbose output

  --logfile <filename>
    Append output to <filename>, in addition to stdout

  --maxdepth <maxdepth>
    Descend only <maxdepth> levels deep into directory structure

  --exec <command>
    Execute <command> if SRC and DEST differ

  --help
    Print this help page and exit

  --version
    Print version and exit

~~~
