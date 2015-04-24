~~~

Backcheck - a recursive file comparison tool

USAGE
  java de.backcheck.Backcheck [OPTIONS] LEFT RIGHT

  Backcheck compares a LEFT file with a RIGHT file.
  If LEFT and RIGHT are both directories, Backcheck compares
  the files in these directories, recursively.
  Backcheck compares files based on file length and file contents.

OPTIONS
  -v --verbose
    Verbose output

  --logfile <filename>
    Log to file (in addition to stdout)

  --maxdepth <maxdepth>
    Descend only <maxdepth> levels deep into directory structure

  --help
    Print this help page and exit

  --version
    Print version and exit

~~~
