/**
 * Copyright (c) 2013, jamiesun, All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 
 *     Redistributions in binary form must reproduce the above copyright notice, this
 *     list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 * 
 *     Neither the name of the {organization} nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.toughradius.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件操作工具类
 */
public class FileUtil
{

    /**
     * 判断文件是否存在(包括目录和文件)
     * 
     * @param path 文件完整路径
     * @return true/false 存在则返回true
     */
    public static boolean isExists(File path)
    {
        if (path == null)
            return false;
        
        return path.exists();
    }
    
    /**
     * 判断文件是否存在(包括目录和文件)
     * 
     * @param path 文件完整路径
     * @return true/false 存在则返回true
     */
    public static boolean isExists(String path)
    {
        if (ValidateUtil.isEmpty(path))
            return false;

        try
        {
            File file = new File(path);
            return file.exists();
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * 判断路径是否为目录
     * 
     * @param path 要判断的完整路径
     * @return true/false 如果是目录，返回true
     */
    public static boolean isDirectory(String path)
    {
        if (ValidateUtil.isEmpty(path))
            return false;
        
        File file = null;
        try
        {
            file = new File(path);
        }
        catch (Exception ex)
        {
            return false;
        }

        return isDirectory(file);
    }
    
    /**
     * 判断路径是否为目录
     * 
     * @param file 文件对象
     * @return true/false 如果是目录，返回true
     */
    public static boolean isDirectory(File file)
    {
        if (file == null)
            return false;
        
        if (!file.exists())
            return false;
        
        return file.isDirectory();
    }

    /**
     * 读取目录的所有文件子目录列表
     * 
     * @param dirPath 目录路径
     * @param 文件目录列表
     */
    public static String[] getDirectoryList(String dirPath)
    {
        if (ValidateUtil.isEmpty(dirPath))
            return null;

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory())
            return null;

        String[] fileNames = dir.list();
        dir = null;

        return fileNames;
    }

    /** 
     * 递归初始化文件列表 
     * 
     * @param fileList 传入的文件列表引用，需先new，不允许为null;
     * @param dir 指定搜索目录
     * @param suffix 后缀或以该结尾相同的数据,调用endsWith进行匹配
     */
    public static void qFileListBySuffix(List<File> fileList, File dir, String suffix)
    {
        File[] list = dir.listFiles();
        for (int i=0;i<list.length;i++)
        {
            File file = list[i];
            if (file.isFile())
            {
                if (file.getName().endsWith(suffix))
                    fileList.add(file);
            }
            else
            {
                qFileListBySuffix(fileList, file, suffix);
            }
        }
    }
    
    /**
     * 拷贝目录 成功返回false,不是目录或dest路径书写错误返回false
     * 
     * @param src 源目录
     * @param dest 目标目录
     * @return 成功返回false,不是目录或dest路径书写错误返回false
     */
    public static boolean copyDirectory(File src, String dest) throws Exception
    {
        if ((src == null) || !src.isDirectory())
            return false;

        try
        {
            File destRoot = new File(dest);
            if (!destRoot.exists())
                destRoot.mkdir();

            File[] entries = src.listFiles();
            int len = entries.length;
            for (int i = 0; i < len; i++)
            {
                File entry = entries[i];
                String target = dest + StringUtil.getSystemSeparator()+ entry.getName();
                if (entry.isDirectory())
                {
                    copyDirectory(entry, target); //递归
                }
                else
                {
                    File toFile = new File(target);
                    FileUtil.copyFile(entry, toFile);
                }
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        return true;
    }
    
    /**
     * 删除文件夹
     * 
     * @param directory 文件夹路径
     * @return true/false
     */
    public static boolean deleteDirectory(String dir, String[] forbidDirs)throws Exception
    {
        if (ValidateUtil.isEmpty(dir))
            return false;

        return deleteDirectory(new File(dir), forbidDirs);
    }

    /**
     * 删除文件夹
     * 
     * @param directory 文件夹象
     * @param forbidDirs 禁止删除的目录
     * @return true/false
     */
    public static boolean deleteDirectory(File dir, String[] forbidDirs) throws Exception
    {
        if ((dir == null) || !dir.isDirectory())
            return false;

        File[] entries = dir.listFiles();
        int sz = entries.length;

        for (int i = 0; i < sz; i++)
        {
            boolean forbid = false;

            if (entries[i].isDirectory())
            {
                if (forbidDirs != null && forbidDirs.length > 0)
                {
                    String folderName = entries[i].getName();
                    for (int j = 0; j < forbidDirs.length; j++)
                    {
                        if (forbidDirs[j].equals(folderName))
                        {
                            forbid = true;
                            break;
                        }
                    }
                }

                if (forbid == true)
                    continue;

                deleteDirectory(entries[i], forbidDirs);
            }
            else
            {
                entries[i].delete();
            }
        }

        dir.delete();
        return true;

    }
    
    /**
     * 判断文件是否存在
     * 
     * @param fileName 文件完整路径
     * @return true/false 存在则返回true
     */
    public static boolean isFile(String fileName)
    {
        if (ValidateUtil.isEmpty(fileName))
            return false;
        
        File file = null;
        try
        {
            file = new File(fileName);
        }
        catch (Exception ex)
        {
            return false;
        }

        return isFile(file);
    }

    /**
     * 判断文件是否存在
     * 
     * @param file File
     * @return true/false 存在则返回true
     */
    public static boolean isFile(File file)
    {
        if (file == null)
            return false;
        
        if (!file.exists())
            return false;
        
        return file.isFile();
    }

    /**
     * 文件拷贝,要检查是否是文件
     * 
     * @param fromFile 源文件对象
     * @param toFile 目标文件对象
     * @return true/false
     */
    public static boolean copyFile(File fromFile, File toFile) throws Exception
    {
        if (!isFile(fromFile) || toFile == null)
            return false;

        FileInputStream fis = new FileInputStream(fromFile);
        int len = fis.available();
        byte[] buf = new byte[len];
        for (int i=0,b=fis.read();b!=-1;i++,b=fis.read())
        {
            buf[i] = (byte)b;
        }
        fis.close();
        fis = null;
        
        FileOutputStream fos = new FileOutputStream(toFile);
        fos.write(buf);
        fos.flush();
        fos.close();
        fos = null;
        return true;
    }
    
    /**
     * 文件拷贝类,输入路径
     * 
     * @param fromFile 源文件路径
     * @param toFile 目标文件路径
     * @return true/false
     */
    public static boolean copyFile(String fromFile, String toFile) throws Exception
    {
        if (ValidateUtil.isEmpty(fromFile) || ValidateUtil.isEmpty(toFile))
            return false;

        File from = new File(fromFile);
        File to = new File(toFile);

        return copyFile(from, to);
    }

    /**
     * 文件拷贝,输入源文件路径,目标目录,文件名
     * 
     * @param fromFile 源文件路径
     * @param toDir 目标目录
     * @param fileName 目录文件
     * @return true/false
     */
    public static boolean copyFile(String fromFile, String toDir, String fileName) throws Exception
    {
        if (ValidateUtil.isEmpty(fromFile) || ValidateUtil.isEmpty(toDir) || ValidateUtil.isEmpty(fileName))
            return false;

        File from = new File(fromFile);
        if (!from.exists() || !from.isFile())
            return false;
        
        //新建目录
        if (!ValidateUtil.isEmpty(toDir))
        {
            File dir = new File(toDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            dir = null;
        }
        
        if (toDir.lastIndexOf(StringUtil.getSystemSeparator()) != toDir.length())
            toDir += StringUtil.getSystemSeparator();
        
        File to = new File(toDir + fileName);
        return copyFile(from, to);
    }

    /**
     * 删除文件,如果路径为空或文件不存在则返回false
     * 
     * @param filePath 文件完整路径
     * @return true/false 成功删除则返回true
     */
    public static boolean deleteFile(String filePath)
    {
        if (ValidateUtil.isEmpty(filePath))
            return false;

        File file = new File(filePath);
        if (!file.exists())
            return false;

        return file.delete();
    }
    
    /**
     * 读取文件内容，并将内容以字符串形式输出。 如果文件不存在，或路径错误，则返回空字符对象
     * 
     * @param fileName 文件名称
     * @return 文件内容
     */
    public static String readResource(Class<?> classPath, String resourceName, String encoding)
    {
        if (ValidateUtil.isEmpty(resourceName) || ValidateUtil.isEmpty(encoding))
            return null;

        try
        {
            InputStream is = classPath.getResourceAsStream(resourceName);
            int len = is.available();
            byte[] bytes = new byte[len];
            for (int i=0,b=is.read();b!=-1;i++,b=is.read())
            {
                bytes[i] = (byte)b;
            }
            is.close();
            is = null;
            
            String fileContent = new String(bytes, encoding);
            return fileContent;
        }
        catch (IOException ex)
        {
            return null;
        }
    }

    /**
     * 读取文件内容，并将内容以字符串形式输出。 如果文件不存在，或路径错误，则返回空字符对象
     * 
     * @param fileName 文件名称
     * @return 文件内容
     */
    public static String readFile(String fileName, String encoding)
    {
        if (!isFile(fileName))
            return null;

        try
        {
            FileInputStream fis = new FileInputStream(fileName);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            byte[] bytes = new byte[1024];int len = -1;
            while ((len = fis.read(bytes)) > 0)
            {
                buf.write(bytes, 0, len);
            }
            
            fis.close();
            fis = null;
            
            String fileContent = new String(buf.toByteArray(), encoding);
            return fileContent;
        }
        catch (IOException ex)
        {
            return null;
        }
    }
    
    /**
     * 根据文件路径,名称,把内容以字节数组的方式写入到指定的文件中
     * 
     * @param fileName 文件完整路径
     * @param in 文件流
     * @return true/false 写入操作成功，返回true
     */
    public static long writeBytesReturnSize(String fileName, InputStream in)
    {
        try
        {
            byte[] content = new byte[in.available()];
            in.read(content);
            
            return writeBytesReturnSize(fileName, in);
        }
        catch(Exception e)
        {
            return 0;
        }
    }
    
    /**
     * 根据文件路径,名称,把内容以字节数组的方式写入到指定的文件中
     * 
     * @param fileName 文件完整路径
     * @param content 文件内容
     * @return true/false 写入操作成功，返回true
     */
    public static long writeBytesReturnSize(String fileName, byte[] content)
    {
        /* 路径不存在,返回 */
        if (ValidateUtil.isEmpty(fileName))
            return 0;

        try
        {
            // 获取文件所在目录,检查,如果不存在目录则创建
            String filePath = StringUtil.getFilePath(fileName);
            File file = new File(filePath);
            if (!file.exists())
                file.mkdirs();

            // 写入文件
            file = new File(fileName);
            synchronized (file)
            {
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(content);
                fos.flush();
                fos.close();
            }
            long size = file.length();
            file = null;

            return size;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    /**
     * 根据文件路径,名称,把内容以字节数组的方式写入到指定的文件中
     * 
     * @param fileName 文件完整路径
     * @param content 文件内容
     * @return true/false 写入操作成功，返回true
     */
    public static boolean writeBytes(String fileName, byte[] content)
    {
        if (ValidateUtil.isEmpty(fileName))
            return false;

        try
        {
            // 获取文件所在目录,检查,如果不存在目录则创建
            String filePath = StringUtil.getFilePath(fileName);
            File file = new File(filePath);
            if (!file.exists())
                file.mkdirs();

            // 写入文件
            file = new File(fileName);
            synchronized (file)
            {
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(content);
                fos.flush();
                fos.close();
            }
            file = null;
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * 根据文件路径,名称,把内容以流的方式写入到指定的文件中
     * 
     * @param fileName 文件完整路径
     * @param content 文件内容
     * @return true/false 写入操作成功，返回true
     */
    public static boolean write(String fileName, String content)
    {
        if (ValidateUtil.isEmpty(fileName))
            return false;

        try
        {
            // 获取文件所在目录,检查,如果不存在目录则创建
            String filePath = StringUtil.getFilePath(fileName);
            File file = new File(filePath);
            if (!file.exists())
                file.mkdirs();

            // 写入文件
            file = new File(fileName);
            synchronized (file)
            {
                FileOutputStream fos = new FileOutputStream(fileName);
                byte[] b = content.getBytes();
                fos.write(b);
                fos.flush();
                fos.close();
            }
            file = null;
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * 根据文件路径,名称,把内容以字符的方式写入到指定的文件中
     * 
     * @param fileName 文件完整路径
     * @param content 文件内容
     * @return true/false 写入操作成功，返回true
     */
    public static boolean writeString(String fileName, String content)
    {
        if (ValidateUtil.isEmpty(fileName))
            return false;

        try
        {
            // 获取文件所在目录,检查,如果不存在目录则创建
            String filePath = StringUtil.getFilePath(fileName);
            File file = new File(filePath);
            if (!file.exists())
                file.mkdirs();

            // 写入文件
            file = new File(fileName);

            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();

            file = null;
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    
    /**
     * 解压ZIP文件
     * 
     * @param zip ZIP文件
     * @param toDir 解压目录
     * @return　返回当前列表
     * @throws IOException
     */
    public static List<File> unzip(File zip, File toDir) throws IOException 
    {
        List<File> files = new ArrayList<File>();
        ZipFile zf = new ZipFile(zip);
        Enumeration<?> entries = zf.entries();
        while (entries.hasMoreElements()) 
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.isDirectory()) 
            {
                new File(toDir, entry.getName()).mkdirs();
                continue;
            }

            InputStream input = null;
            OutputStream output = null;
            try 
            {
                File f = new File(toDir, entry.getName());
                input = zf.getInputStream(entry);
                output = new FileOutputStream(f);
                Streams.copy(input, output);
                files.add(f);
            } 
            finally 
            {
                try{if(input != null)input.close();}catch(Exception e){};
                try{if(output != null)output.close();}catch(Exception e){};
            }
        }
        return files;
    }
    
    /**
     * 解压GZIP文件
     * 
     * @param gzip
     * @param toDir
     * @return
     * @throws IOException
     */
    public static File ungzip(File gzip, File toDir) throws IOException 
    {
        toDir.mkdirs();
        File out = new File(toDir, gzip.getName());
        GZIPInputStream input = null;
        FileOutputStream output = null;
        
        try 
        {
            FileInputStream fin = new FileInputStream(gzip);
            input = new GZIPInputStream(fin);
            output = new FileOutputStream(out);
            Streams.copy(input, output);
        } 
        finally 
        {
            try{if(input != null)input.close();}catch(Exception e){};
            try{if(output != null)output.close();}catch(Exception e){};
        }
        
        return out;
    }
}