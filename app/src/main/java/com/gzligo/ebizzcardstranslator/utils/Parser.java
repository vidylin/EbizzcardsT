package com.gzligo.ebizzcardstranslator.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;


/**
 * A class of static methods that encapsulate XML into a XmlElementNode object<br>
 * <p>
 * Created by LiWang on 2015-12-4<br>
 * Copyright(c) 2015 GuangZhou LiGo Information Technology Co.,Ltd
 */
public class Parser {
    private final static String MES = "mes";
    private final static String ELS = "els";

    private final static String TAG = "Parser";
    private static String mElementName;

    private static boolean isJason = false;

    public static void setElementNode(String elementName) {
        mElementName = elementName;
    }

    /**
     * 解析xml文件，当根节点只有一个时
     *
     * @param xml
     * @param endName
     * @return
     */
    public static XmlElementNode<String, Object> parse(String xml, String endName) {
        XmlElementNode<String, Object> xmlnode = null;
        try {
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(new ByteArrayInputStream(xml.getBytes()), "utf-8");
            xmlnode = parse(pullParser, endName);
        } catch (OutOfMemoryError e) {
            Log.e("Parser", "CommonParser is OutOfMemoryError");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlnode;
    }

    /**
     * 解析xml
     *
     * @param xml
     * @return 组装好的树状数据，XmlElementNode<String,Object>
     * Key为xml的标签名，Value是泛型的标签内容，有可能为基础数据类型
     * ，XmlElementNode<String,Object>和ArrayList<Object>
     */
    public static XmlElementNode<String, Object> parse(String xml) {
        if (xml == null || xml.isEmpty()) {
            return null;
        }
        XmlElementNode<String, Object> xmlnode = null;
        try {
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(new ByteArrayInputStream(xml.getBytes()), "utf-8");
            xmlnode = parse(pullParser, null);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "CommonParser is OutOfMemoryError");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlnode;
    }


    public static XmlElementNode<String, Object> parse(InputStream xml) {
        if (xml == null) {
            return null;
        }
        XmlElementNode<String, Object> xmlnode = null;
        try {
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(xml, "utf-8");
            xmlnode = parse(pullParser, null);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "CommonParser is OutOfMemoryError");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlnode;
    }

    public static XmlElementNode<String, Object> parse(XmlPullParser pullParser) {
        if (pullParser == null) {
            return null;
        }
        XmlElementNode<String, Object> xmlnode = null;
        try {
            xmlnode = parse(pullParser, null);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "CommonParser is OutOfMemoryError");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlnode;
    }


    /**
     * 解析整个XML文件，mes和les标签被单独处理，其他标签下的数据将被解析为一个XmlElementNode的实例 注：正确的文件格式应该为<br>
     * &ltmes&gt <br>
     * &nbsp&nbsp &ltles&gt <br>
     * &nbsp&nbsp&nbsp&nbsp data <br>
     * &nbsp&nbsp &lt/les&gt <br>
     * &lt/mes&gt <br>
     * 且les下只有一个元素
     *
     * @param xpp
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static XmlElementNode<String, Object> parse(XmlPullParser xpp, String endName)
            throws XmlPullParserException, IOException {
        int event = xpp.getEventType();
        boolean isContinue = true;
        XmlElementNode<String, Object> result = new XmlElementNode<String, Object>();
        while (isContinue) {
            switch (event) {
                // 开始标签，略过
                case XmlPullParser.START_DOCUMENT:
                    break;
                // 开始标签，检查是否为mes和les元素
                case XmlPullParser.START_TAG:
                    String tag = xpp.getName();
                    if (tag.equals(MES)) {
                        //存入mes中的code信息
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            result.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));
                        }
                    } else if (tag.equals(ELS)) {
                        // TODO：处理els中的信息
                        if (isJason) {
                            try {
                                result.put(tag, xpp.nextText());
                            } catch (Exception e) {
                                Log.e(TAG, "parse deal els info Exception " + e.toString());
                            }
                        }
                    } else {
                        // 否则开始处理正式的信息
                        XmlElementNode<String, Object> rootElementNode = parseElement(xpp, endName);
                        if (rootElementNode == null) {
                            return null;
                        }
                        Set<Entry<String, Object>> set = rootElementNode.entrySet();
                        for (Entry<String, Object> entry : set) {
                            result.put(entry.getKey(), entry.getValue());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                case XmlPullParser.END_DOCUMENT:
                    isContinue = false;
                    break;
                default:
                    break;
            }
            // 未结束就取下一个元素
            if (isContinue) {
                event = xpp.next();
            }
        }
        return result;
    }

    ;

    /**
     * 解析具体某一个元素下的内容
     *
     * @param xpp 包含输入数据的XmlPullParser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static XmlElementNode<String, Object> parseElement(XmlPullParser xpp, String endName)
            throws XmlPullParserException, IOException {
        int event = xpp.getEventType();
        boolean isContinue = true;
        /** 根节点 */
        XmlElementNode<String, Object> root = new XmlElementNode<String, Object>();
        /** 当前正在处理的节点 */
        XmlElementNode<String, Object> correntNode = null;
        /** 当前正在处理的元素的标签 */
        String currentTag = null;
        /** 元素开始标签栈，层次深的标签在上 */
        Stack<String> startTags = new Stack<String>();
        /** 判断是否为叶元素的控制符 */
        boolean isLeaf = false;
        /** 记录当前叶元素的文本值 */
        String text = null;
        while (isContinue) {
            switch (event) {
                // 开始元素事件，在此处理标签入栈及进入下层元素等操作
                case XmlPullParser.START_TAG:
                    // 更新当前标签
                    currentTag = xpp.getName();

                    // 暂时不知道当前节点是否为叶节点，默认不是
                    isLeaf = false;
                    // 遇到顶层开始标签，将当前节点设为根节点
                    if (startTags.isEmpty()) {
                        correntNode = root;
                    }
                    // 非顶层开始标签，则新建一个节点，设置其父节点为当前节点，但暂时不加入当前节点的Map。
                    // 然后将当前节点设为新增的节点
                    else {
                        XmlElementNode<String, Object> son = new XmlElementNode<String, Object>();
                        son.setFather(correntNode);
                        correntNode = son;
                    }
                    correntNode.tag = currentTag;
                    startTags.push(currentTag);
                    break;
                // TEXT事件，说明当前正在操作的元素是一个叶节点，为当前节点的父节点的Map增加一个键值对
                case XmlPullParser.TEXT:
                    text = xpp.getText();
                    // 知道当前元素是叶节点,标记
                    if (!TextUtils.isEmpty(text) && !text.trim().isEmpty()) {
                        isLeaf = true;
                    } else {
                        text = null;
                    }
                    break;
                // 结束元素事件，在此处理标签出栈几返回上层元素等操作
                case XmlPullParser.END_TAG: // 结束元素事件
                    currentTag = xpp.getName();
                    String peek = startTags.peek();
                    // 做一个保护，防止XML的开始标签和结束标签没有一一对应
                    if (peek.equals(currentTag)) {
                        // 如果当前节点不是叶节点，则将当前节点加入父节点的Map中，
                        if (!isLeaf) {
                            correntNode.putInFatherMap(currentTag);
                        }
                        // 若是叶节点，则将刚才记录的字符串放入
                        else {
                            if (text != null) {
                                correntNode.putInFatherMap(currentTag, text);
                            }
                            text = null;
                            isLeaf = false;
                        }
                        // 将当前操作的节点回溯
                        correntNode = correntNode.getFather();
                        startTags.pop();

                        // 若开始标签栈为空，则停止
                        if (startTags.isEmpty()) {
                            isContinue = false;
                            if (endName != null) {
                                if (currentTag.equals(endName)) {
                                    root = correntNode;
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            // 未结束就取下一个元素
            if (isContinue) {
                event = xpp.next();
            }
        }
        return root;
    }


    public static InputStream getStringStream(String xml) {
        if (xml != null && !xml.trim().equals("")) {
            ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(xml.getBytes());
            return tInputStringStream;
        }
        return null;
    }

    public static void isJason(boolean jason) {
        isJason = jason;
    }

}
