package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.Normalizer;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;

public class Main
{
    // UTF-8 인코딩
    private static final String ENCODE = "utf-8";
    
    // Notion 해쉬값을 변경할 정규식
    private static final String PATTERN = "([ .a-z0-9+]){33}";
    
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        //        String zipFilename = "D:/sicument/TestWorkspace/Export-5ec30bba-214c-463e-b986-aa805233cbf7.zip";
        //        String decompressFilename = "D:/sicument/TestWorkspace/Export-5ec30bba-214c-463e-b986-aa805233cbf7.zip";
        //        System.out.println("<< 시작 >>");
        //        File input = new File("D:/sicument/TestWorkspace/Export-5ec30bba-214c-463e-b986-aa805233cbf7");
        //        //        File input = new File(args[0]);
        //        File output = new File(input.getParentFile().getAbsolutePath());
        //        copy(input, output);
        //        System.out.println("<< 완료 >>");
        
        try
        {
            decompress("D:/sicument/TestWorkspace/Export-5ec30bba-214c-463e-b986-aa805233cbf7.zip", "D:/sicument/TestWorkspace/Export-5ec30bba-214c-463e-b986-aa805233cbf7");
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * 파일복사
     * 
     * @param sourceFile
     *            복사 될 파일
     * @param targetFile
     *            복사 파일
     */
    public static void copy(File sourceFile, File targetFile)
    {
        // 타겟폴더가 존재하지 않으면 생성
        if (!targetFile.exists())
        {
            targetFile.mkdir();
        }
        
        File[] ff = sourceFile.listFiles();
        for (File file : ff)
        {
            String path = "";
            String tempFileStr = file.getName();
            
            Optional<String> ext = getExtensionByStringHandling(tempFileStr);
            if (ext.isPresent())
            {
                if ("md".equals(ext.get()))
                {
                    path = targetFile.getAbsolutePath() + File.separator + tempFileStr.substring(0, tempFileStr.length() - 35).trim() + ".md";
                }
                else
                {
                    path = targetFile.getAbsolutePath() + File.separator + tempFileStr;
                }
            }
            else
            {
                path = targetFile.getAbsolutePath() + File.separator + tempFileStr.substring(0, tempFileStr.length() - 32).trim();
            }
            
            File temp = new File(path);
            if (file.isDirectory())
            {
                temp.mkdir();
                copy(file, temp);
            }
            else
            {
                FileInputStream fis = null;
                FileOutputStream fos = null;
                try
                {
                    fis = new FileInputStream(file);
                    fos = new FileOutputStream(temp);
                    byte[] b = new byte[4096];
                    int cnt = 0;
                    while ((cnt = fis.read(b)) != -1)
                    {
                        fos.write(b, 0, cnt);
                    }
                    convertFile(temp);
                }
                catch (IllegalArgumentException e)
                {
                    
                }
                catch (IOException e)
                {
                    System.out.println("에러가 발생했습니다. : " + e.getMessage());
                }
                finally
                {
                    try
                    {
                        fis.close();
                        fos.close();
                    }
                    catch (IOException e)
                    {
                        System.out.println("에러가 발생했습니다. : " + e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @param file
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @SuppressWarnings({ "unused" })
    private static void convertFile(File file) throws IOException, IllegalArgumentException
    {
        FilenameUtils filenameUtils = new FilenameUtils();
        String extension = FilenameUtils.getExtension(file.getName());
        // 마크다운 파일만 변환
        if ("md".equals(extension))
        {
            // 인코딩된 마크다운 파일
            String encodedContents = getEncodedMarkdownContent(file);
            // 디코딩된 마크다운 파일(1. URLDecode, 한글 자소합치기)
            String decodedContents = getDecodeMarkdownContent(encodedContents);
            // 기존 파일에 덮어쓰기
            Files.write(Paths.get(file.getAbsolutePath()), decodedContents.replaceAll(PATTERN, "").getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
    
    /**
     * 파일 확장자 체크
     * 
     * @param fileName
     * @return null == Driectory, others == ext
     */
    private static Optional<String> getExtensionByStringHandling(String fileName)
    {
        return Optional.ofNullable(fileName).filter(f -> f.contains(".")).map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }
    
    /**
     * 
     * @param zipFileName
     * @param directory
     * @throws Throwable
     */
    public static void decompress(String zipFileName, String directory) throws Throwable
    {
        File zipFile = new File(zipFileName);
        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry zipentry = null;
        try
        {
            // 파일 스트림
            fis = new FileInputStream(zipFile);
            // Zip 파일 스트림
            zis = new ZipInputStream(fis);
            // Entry가 없을때까지 반복
            while ((zipentry = zis.getNextEntry()) != null)
            {
                String filename = zipentry.getName();
                File file = new File(directory, getDecodeMarkdownContent(filename));
                // Entry가 폴더면 폴더 생성
                if (zipentry.isDirectory())
                {
                    file.mkdirs();
                }
                else
                {
                    // 파일이면 파일 만들기
                    createFile(file, zis);
                }
            }
        }
        catch (Throwable e)
        {
            throw e;
        }
        finally
        {
            if (zis != null)
            {
                zis.close();
            }
            if (fis != null)
            {
                fis.close();
            }
        }
    }
    
    /**
     * 
     * @param file
     * @param zis
     * @throws Throwable
     */
    private static void createFile(File file, ZipInputStream zis) throws Throwable
    {
        // 상위 디렉토리 확인
        File parentDir = new File(file.getParent());
        // 상위 디렉토리가 존재하지 않으면 생성
        if (!parentDir.exists())
        {
            parentDir.mkdirs();
        }
        // 파일 스트림 선언
        try (FileOutputStream fos = new FileOutputStream(file))
        {
            byte[] buffer = new byte[256];
            int size = 0;
            // Zip스트림으로부터 byte뽑아내기
            while ((size = zis.read(buffer)) > 0)
            {
                // byte로 파일 만들기
                fos.write(buffer, 0, size);
            }
        }
        catch (Throwable e)
        {
            throw e;
        }
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException
     */
    private static String getEncodedMarkdownContent(File file) throws IOException
    {
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), Charset.defaultCharset()); // contents 는 전체 파일 내용
    }
    
    /**
     * 
     * @param filename
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getDecodeMarkdownContent(String filename) throws UnsupportedEncodingException
    {
        return hangulJasoMerge(URLDecoder.decode(filename, ENCODE));
    }
    
    /**
     * 
     * 
     * @param content
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String hangulJasoMerge(String content) throws UnsupportedEncodingException
    {
        return Normalizer.normalize(content, Normalizer.Form.NFC);
    }
    
    //    private void dummyCode()
    //    {
    //        String[] array = contents2.split("\\n");
    //        for (String s : array)
    //        {
    //            if (Pattern.matches(pattern, s))
    //            {
    //                System.out.println(s.replaceAll(pattern3, ""));
    //            }
    //        }
    //        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    //        String line = null;
    //        while ((line = reader.readLine()) != null)
    //        {
    //            if (Pattern.matches(pattern, line))
    //            {
    //                String temp = line.split("]")[1];
    //                String temp2 = temp.substring(1, temp.length() - 1);
    //                String result = URLDecoder.decode(temp2, "utf-8");
    //                String result2 = Normalizer.normalize(result, Normalizer.Form.NFC);
    //                String[] result3 = result2.split(" ");
    //                for (String s : result3)
    //                {
    //                    if (s.length() > 32)
    //                    {
    //                        System.out.println(s);
    //                    }
    //                }
    //            }
    //            
    //        }
    //    }
}