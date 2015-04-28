~~~

Backcheck - a recursive file comparison and verification tool

SYNOPSIS

  java de.backcheck.Backcheck compare [OPTIONS] SRC DEST
  java de.backcheck.Backcheck record  [OPTIONS] SRC DEST
  java de.backcheck.Backcheck verify  [OPTIONS] SRC DEST

DESCRIPTION

  If started with 'compare', Backcheck compares a SRC file with a DEST file.
  If SRC and DEST are both directories, Backcheck compares
  the files in these directories, recursively.

  If started with 'record', Backcheck traverses SRC, calculates
  each file's length and checksum, and writes them to DEST file.

  If started with 'verify', Backcheck loads lengths and checksums
  from SRC (a file created with 'record') and compares them
  with DEST, recursively.

  The record/verify mode can be used for taking a snapshot of an
  existing directory structure and later verify that the files in
  that directory structure did not change.
  You may find it useful for checking read-only archives.

  Backcheck returns the following exit codes:
    0  Success/No differences found
    1  One or more differences found
    2  Other error

OPTIONS
  -v --verbose
    Verbose output

  --logfile <filename>
    Append log output to <filename>, in addition to stdout

  --maxdepth <maxdepth>
    Descend only <maxdepth> levels deep into directory structure
    (for 'compare' and 'record')

  --help
    Print this help page and exit

  --version
    Print version and exit

EXAMPLES

  java de.backcheck.Backcheck compare /foobar /backups/foobar
    Compares /foobar with /backups/foobar, recursively, and reports
    each difference

  java de.backcheck.Backcheck record /backups/archive /home/joe/digest
    Records the checksum of each file in /backups/archive and writes the
    results to /home/joe/digest

  java de.backcheck.Backcheck verify /home/joe/digest /backups/archive
    Verifies for each entry in /home/joe/digest that the same file
    exists in /backups/archive and has the same checksum

~~~
