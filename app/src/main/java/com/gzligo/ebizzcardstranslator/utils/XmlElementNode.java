package com.gzligo.ebizzcardstranslator.utils;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 继承自HashMap的类，存放解析好的XML数据的中间数据结构<br>
 * 本公司定义好的XML有三种数据类型，复合元素（包含子元素），叶元素（包含基础元素）和元素数组。<br>
 * 我们使用实例化的XmlElementNode<String,Object>来存放这些数据类型。这个实例是树状的<br>
 * -对一个元素，如果其子元素是一个基础数据，则将它解析成一个String。以XML元素的name为Key，text为Value，放入map中。<br>
 * -如果其子元素为一个复合元素，则将它解析成一个XmlElementNode，以XML元素的name为Key，该实例为Value，放入map中。<br>
 * -如果其子元素为一个数组，则将它解析成一个XmlElementNode，这个XmlElementNode的Map内只有一个元素，是一个ArrayList&
 * ltXmlElementNode&gt。 这是因为，在我们的定义里面，数组元素是数组的子元素，而不是父元素的子元素。
 * 即magazineDirectory是magazineDirectories的子元素，
 * magazineDirectories才是magazine的子元素。
 * 如果我们直接把magazineDirectories解析成一个ArrayList&ltXmlElementNode&gt，等于是把
 * magazineDirectory作为magazine的子元素
 * （ArrayList并不是树的一个节点，只是当前节点子节点的一种组织方式），这与XML结构是不对应的。
 * 而且，我们解析的时候，并不能具体知道哪一个元素是数组
 * ，如果magazineDirectories只有一个元素，则必须作为复合元素处理，magazineDirectory依然是第三级。
 *
 * @author Chenyuning
 * @param <K>
 *            一般是String
 * @param <V>
 *            一般是Object
 */
@SuppressWarnings("serial")
public class XmlElementNode<K, V> extends HashMap<K, V> {
    private XmlElementNode<String, Object> mFather = null;
    public String tag = null;

    public void setFather(XmlElementNode<String, Object> father) {
        mFather = father;
    }

    public XmlElementNode<String, Object> getFather() {
        return mFather;
    }

    @SuppressWarnings("unchecked")
    public void putInFatherMap(String tag, String text) {
        if (mFather == null) {
            if (text == null){
                text ="NULL";
            }
            mFather = new XmlElementNode<String, Object>();
            mFather.put(tag, text);
        }


        Object oldValue = mFather.get(tag);
        if (oldValue == null) {
            mFather.put(tag, text);
        } else if (oldValue instanceof ArrayList) {
            ((ArrayList<String>) oldValue).add(text);
        } else {
        }
    }

    @SuppressWarnings("unchecked")
    public void putInFatherMap(String tag) {
        if (mFather != null) {
            Object object = mFather.get(tag);
            if (object == null) {
                mFather.put(tag, this);
            }
            else if (object instanceof ArrayList) {
                ArrayList<Object> list = (ArrayList<Object>) object;
                list.add(this);
            }
            else {

                ArrayList<Object> list = new ArrayList<Object>();
                list.add(object);
                list.add(this);
                mFather.put(tag, list);
            }
        }
    }

    public Integer getInt(String key) {
        String value = getString(key);
        Integer i = 0;
        if (value != null) {
            try {
                i = Integer.valueOf(value);
            } catch (NumberFormatException e) {
            }
        }
        return i;
    }

    public Long getLong(String key) {
        String value = getString(key);
        Long i = 0l;
        if (value != null) {
            try {
                i = Long.valueOf(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public Double getDouble(String key) {
        String value = getString(key);
        double d = 0.0d;
        try {
            if (value != null) {
                d = Double.valueOf(value).doubleValue();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return d;
    }

    public Float getFloat(String key) {
        String value = getString(key);
        float f = 0.0f;
        try {
            if (value != null) {
                f = Float.valueOf(value).floatValue();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return f;
    }

    public Boolean getBoolean(String key) {
        String value = getString(key);
        if (value != null && "true".equals(value.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    public String getString(String key) {
        Object o = get(key);
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return "";
        }
    }


    @SuppressWarnings("unchecked")
    public XmlElementNode<String, Object> getXmlNode(String key) {
        return (XmlElementNode<String, Object>) get(key);
    }


    @SuppressWarnings("unchecked")
    public ArrayList<Object> getArrayList(String key) {
        Object o = get(key);
        if (o == null) {
            return new ArrayList<Object>();
        } else if (o instanceof ArrayList) {
            return (ArrayList<Object>) o;
        } else {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(o);
            return list;
        }
    }

    @SuppressWarnings("unchecked")
    public void logData() {
        Log.d("CYN", tag);
        Set<K> keySet = (Set<K>) keySet();
        for (Iterator<K> it = keySet.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = get(key);
            Log.d("CYN", key);
            if (value == null) {
                Log.d("CYN", "NULL");
            } else if (value instanceof String) {
                Log.d("CYN", (String) value);
            } else if (value instanceof XmlElementNode) {
                ((XmlElementNode<K, V>) value).logData();
            } else if (value instanceof ArrayList) {
                ArrayList<XmlElementNode<K, V>> list = (ArrayList<XmlElementNode<K, V>>) value;
                for (XmlElementNode<K, V> node : list) {
                    node.logData();
                }
            }
        }
    }
}

