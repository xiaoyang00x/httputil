package com.httputil.apitest.util;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jiaxiong
 * @date 2019-09-26 15:26
 */
public class XmlUtils {

    private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    /**
     * 读取xml文件
     */
    public static String readXml(String xmlPath) {
        Document document = load(xmlPath);
        // document转String
        return document.asXML();
    }

    /**
     * 读取xml文件
     *
     * @param xmlPath xml文件路径
     */
    private static Document load(String xmlPath) {
        File file = new File(xmlPath);
        Document document = null;
        SAXReader reader = new SAXReader();
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            logger.error("更新xml文件", e);
        }
        return document;
    }

    /**
     * 暂时不支持更改output节点之外的节点数据
     *
     * @param nodeXpath "/root/output/day_views/day_view/registry_list/registry/count"
     * @param targetStr 属性更改后的值
     * @param xmlPath   xml文件路径，"src/main/resources/xml/xxx.xml"
     * @param itemIndex 需要更新的第几个节点
     * @throws Exception
     */
    public static void updateXmlData(String nodeXpath, String targetStr, String xmlPath,
            int itemIndex) {
        Document document = load(xmlPath);
        List list = document.selectNodes(nodeXpath);
        // 更新指定的某一个元素
        if (list.size() > itemIndex) {
            Element element = (Element) list.get(itemIndex);
            element.setText(targetStr);
        }
        // 保存文件的修改
        saveXmlFile(document, xmlPath);
    }

    /**
     * 保存xml文件
     */
    private static void saveXmlFile(Document document, String xmlPath) {
        try {
            XMLWriter writer = new XMLWriter(new FileWriter(new File(xmlPath)));
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            logger.error("保存xml文件出错", e);
        }
    }
}
