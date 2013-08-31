#!/usr/bin/python
#codeing:utf-8

import os
from optparse import OptionParser

def convutf8(chdir,cext):
    if not chdir:chdir = '.'
    if not cext:raise Exception('no file ext')
    for base,sub,files in os.walk(chdir):
        for fd in files:
            fpath = os.path.join(base,fd)
            if fd.endswith(cext):
                try:
                    rfd = open(fpath,'rb')
                    print 'read  gbk file',fd
                    gbk_src = rfd.read().decode('gbk')
                    rfd.close()
                    wfd = open(fpath,'wb')
                    print 'write  utf8 file',fd
                    utf8_src = gbk_src.encode('utf-8')
                    if utf8_src:
                        wfd.write(utf8_src)
                except Exception,e:
                    print e
                    print 'already utf-8'

if __name__ == "__main__":
    usage = "usage: %prog [options] arg"
    parser = OptionParser(usage)
    parser.add_option("-p", "--path", dest="path",help="files path")
    parser.add_option("-e", "--ext",dest="ext", help="file ext")
    (options, args) = parser.parse_args()
    convutf8(options.path,options.ext)







