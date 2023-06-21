package com.example.forum.util;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author 言曌 liuyanzhao.com
 * @since 2023/5/16 0:36
 */

/**
 * PDF工具
 *
 * @author ppp
 * @date 2022/8/5
 */
public class PdfUtil {

    static {
        // Velocity初始化
        Velocity.setProperty(RuntimeConstants.OUTPUT_ENCODING, StandardCharsets.UTF_8);
        Velocity.setProperty(RuntimeConstants.INPUT_ENCODING, StandardCharsets.UTF_8);
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }


    /**
     * 据模板生成pfd格式文件
     *
     * @param context      上下文对象
     * @param template     pdf模板
     * @param outputStream 生成ofd文件输出流
     */
    public static void pdfFile(Context context, String template, OutputStream outputStream) {
        try (PdfWriter pdfWriter = new PdfWriter(outputStream)) {
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            ConverterProperties properties = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();
            // 字体设置，解决中文不显示问题
            PdfFont sysFont = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
            fontProvider.addFont(sysFont.getFontProgram(), "UniGB-UCS2-H");
            properties.setFontProvider(fontProvider);

            Template pfdTemplate = Velocity.getTemplate(template, "UTF-8");
            StringWriter writer = new StringWriter();
            pfdTemplate.merge(context, writer);
            HtmlConverter.convertToPdf(writer.toString(), pdfDocument, properties);
            pdfDocument.close();
        } catch (Exception e) {
            throw new RuntimeException("PFD文件生成失败", e);
        }
    }

}