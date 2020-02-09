Kython Bridge
=============

This is a simple bridge library to embed libpython in Kython for compilation purposes.

This library exports a single function:

.. code-block:: c

    const char* compile(const char* code, const char* filename);

The return type is a hex-encoded valid KYC file.
