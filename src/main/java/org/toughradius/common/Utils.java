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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

public class Utils
{

    public final static String subStr(String src, int len)
    {
        if (src.length() <= len) return src;

        return src.substring(0, len - 1);
    }

    public final static String md5Encoder(String s)
    {
        char hexDigits[] =
        { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try
        {
            byte[] strTemp = s.getBytes();
            // 使用MD5创建MessageDigest对象
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++)
            {
                byte b = md[i];
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * ip地址转成整数.
     * 
     * @param ip
     * @return
     */
    public static long ip2long(String ip)
    {
        String[] ips = ip.split("[.]");
        long num = 16777216L * Long.parseLong(ips[0]) + 65536L * Long.parseLong(ips[1]) + 256 * Long.parseLong(ips[2])
                + Long.parseLong(ips[3]);
        return num;
    }

    /**
     * 整数转成ip地址.
     * 
     * @param ipLong
     * @return
     */
    public static String long2ip(long ipLong)
    {
        // long ipLong = 1037591503;
        long mask[] =
        { 0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000 };
        long num = 0;
        StringBuffer ipInfo = new StringBuffer();
        for (int i = 0; i < 4; i++)
        {
            num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0) ipInfo.insert(0, ".");
            ipInfo.insert(0, Long.toString(num, 10));
        }
        return ipInfo.toString();
    }

    public static InetAddress getLocalAddress()
    {
        InetAddress addr = null;
        try
        {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (netInterfaces.hasMoreElements())
            {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                ip = (InetAddress) ni.getInetAddresses().nextElement();
                if (!ip.isLoopbackAddress())
                {
                    addr = ip;
                    break;
                }
            }
            addr = addr != null ? addr : InetAddress.getLocalHost();
        }
        catch (Exception e)
        {
        }
        return addr;

    }

    public static String getLocalIP()
    {
        String ip = "";
        try
        {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements())
            {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals("eth0"))
                {
                    continue;
                }
                else
                {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements())
                    {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address) continue;
                        ip = ia.getHostAddress();
                    }
                    break;
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        return ip;
    }

    public static String getUUID()
    {
        return String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits()));
    }

    public static long toLong(Object oid)
    {
        return Long.parseLong(String.valueOf(oid));
    }

    public static String Html2Text(String inputString)
    {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;

        java.util.regex.Pattern p_html1;
        java.util.regex.Matcher m_html1;

        try
        {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            // }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            // }
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            String regEx_html1 = "<[^>]+";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
            m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll(""); // 过滤html标签

            textStr = htmlStr;

        }
        catch (Exception e)
        {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }

    @SuppressWarnings("unchecked")
    public static HashSet loadClasses(String prefix)
    {
        HashSet classSet = new HashSet();
        String classpath = System.getProperty("java.class.path");
        String split = System.getProperty("path.separator");
        String[] paths = classpath.split(split);
        for (int i = 0; i < paths.length; i++)
        {
            String path = paths[i];
            File file = new File(path);
            if (file.isFile())
            {// jar
                JarFile jarFile;
                try
                {
                    jarFile = new JarFile(path);
                }
                catch (IOException e)
                {
                    continue;
                }
                Enumeration enumeration = jarFile.entries();
                while (enumeration.hasMoreElements())
                {
                    JarEntry entry = (JarEntry) enumeration.nextElement();
                    String name = entry.getName();
                    if (name.endsWith(".class"))
                    {

                        String cname = name.substring(0, name.length() - 6).replaceAll("/", ".");
                        if (cname.startsWith(prefix))
                        {
                            Class clasz;
                            try
                            {
                                clasz = Class.forName(cname);
                            }
                            catch (ClassNotFoundException e)
                            {
                                continue;
                            }
                            classSet.add(clasz);
                        }
                    }
                }
            }
            else if (file.isDirectory())
            {
                List list = new ArrayList();
                initFile(file, list, "class");
                for (int k = 0; k < list.size(); k++)
                {
                    File curFile = (File) list.get(k);
                    String name = curFile.getPath().substring(file.getPath().length(), curFile.getPath().length() - 6);
                    String cname = name.substring(1, name.length()).replaceAll("\\\\", ".");
                    cname = name.substring(1, name.length()).replaceAll("/", ".");
                    if (cname.startsWith(prefix))
                    {
                        Class clasz;
                        try
                        {
                            clasz = Class.forName(cname);
                        }
                        catch (ClassNotFoundException e)
                        {
                            continue;
                        }
                        classSet.add(clasz);
                    }
                }
            }

        }
        return classSet;
    }

    @SuppressWarnings("unchecked")
    private static void initFile(File dir, List fileList, String suffix)
    {
        File[] list = dir.listFiles();
        for (int i = 0; i < list.length; i++)
        {
            File file = (File) list[i];
            if (file.isFile())
            {
                if (file.getName().endsWith(suffix)) fileList.add(file);
            }
            else
            {
                initFile(file, fileList, suffix);
            }
        }
    }

    public static byte[] readFile(String file) throws Exception
    {
        FileChannel fc = null;
        FileInputStream fin = null;
        ByteArrayOutputStream out = null;
        try
        {
            fin = new FileInputStream(file);
            fc = fin.getChannel();
            out = new ByteArrayOutputStream(4096);
            fc.transferTo(0, fc.size(), Channels.newChannel(out));
            return out.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fc != null) fc.close();
            if (fin != null) fin.close();
            if (out != null) out.close();
        }
        return null;
    }

    public static void writeFile(byte[] data, String outfile) throws IOException
    {
        FileOutputStream fout = null;
        FileChannel fc = null;
        try
        {
            fout = new FileOutputStream(outfile);
            fc = fout.getChannel();
            fc.transferFrom(Channels.newChannel(new ByteArrayInputStream(data)), 0, data.length);
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
        finally
        {
            if (fc != null) fc.close();
            if (fout != null) fout.close();
        }

    }

    public static void copyFile(String infile, String outfile)
    {
        try
        {
            FileInputStream fin = new FileInputStream(infile);
            FileOutputStream fout = new FileOutputStream(outfile);

            FileChannel fcin = fin.getChannel();
            FileChannel fcout = fout.getChannel();

            try
            {
                fcout.transferFrom(fcin, 0, fcin.size());
            }
            finally
            {
                fcout.close();
                fout.close();
            }
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }

    }

    public static void writeFile(String body, String outfile) throws Exception
    {
        byte[] bits = null;
        try
        {
            bits = body.getBytes("utf-8");
        }
        catch (Exception e)
        {
            bits = body.getBytes();
        }

        writeFile(bits, outfile);
    }

    public static void compress(String srcPathName, File zipFile) throws IOException
    {
        String path = zipFile.getAbsolutePath();
        if (zipFile.exists())
        {
            zipFile.renameTo(new File(String.format("%s.tmp.%s", path, DateTimeUtil.getDateTimeString())));
            zipFile = new File(path);
        }

        File srcdir = new File(srcPathName);
        if (!srcdir.exists()) throw new RuntimeException(srcPathName + "不存在！");

        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(zipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(srcdir);
        // fileSet.setIncludes("**/*.java"); 包括哪些文件或文件夹
        // eg:zip.setIncludes("*.java");
        // fileSet.setExcludes(...); 排除哪些文件或文件夹
        zip.addFileset(fileSet);
        zip.execute();
    }

    public static void deleteDir(File file)
    {
        if (file.exists())
        {
            File fs[] = file.listFiles();
            if (fs.length <= 0)
            {
                file.delete();
                return;
            }

            for (File file2 : fs)
            {
                if (file2.isFile())
                {
                    file2.delete();
                }
                else
                {
                    deleteDir(file2);
                }
            }

        }
    }

    public static String encodeUrl(String src)
    {
        if (src == null) return null;
        try
        {
            return URLEncoder.encode(src, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return src;
        }
    }

    public static String decodeUrl(String src)
    {
        if (src == null) return null;
        try
        {
            return URLDecoder.decode(src, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return src;
        }
    }

}
