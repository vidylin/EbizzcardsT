package com.gzligo.ebizzcardstranslator.persistence;

import com.gzligo.ebizzcardstranslator.utils.XmlElementNode;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Lwd on 2017/7/6.
 */

public class District extends BaseBean{
    private static final String TAG = District.class.getSimpleName();
    private String localName;
    private String areaNumber;

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getAreaNumber() {
        return areaNumber;
    }

    public void setAreaNumber(String areaNumber) {
        this.areaNumber = areaNumber;
    }

    public District() {
        initDefaultData();
    }

    private void initDefaultData() {
        localName = "";
        areaNumber = "";
    }

    public District(XmlElementNode<String, Object> node) {
        initDefaultData();
        initBasicData(District.class, node);
    }


    public static ArrayList<District> initDistrict(XmlElementNode<String, Object> node) {
        ArrayList<District> districts = new ArrayList<District>();
        try {
            ArrayList<XmlElementNode<String, Object>> arrayList = (ArrayList<XmlElementNode<String, Object>>) node.get("district");
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    District district = new District(arrayList.get(i));
                    districts.add(district);
                }
            }
            return districts;
        } catch (ClassCastException e) {
            e.printStackTrace();
            District district = new District((XmlElementNode<String, Object>) node.get("district"));
            districts.add(district);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return districts;
    }

    @SuppressWarnings("rawtypes")
    private void initBasicData(Class nodeClass, XmlElementNode<String, Object> node) {
        if (node == null) {
            return;
        }
        Field[] fields = this.getClass().getDeclaredFields();
        Field[] parentFields = nodeClass.getSuperclass().getDeclaredFields();
        for (Field field : fields) {
            initValue(field, node);
        }
        for (Field field : parentFields) {
            initValue(field, node);
        }
    }

    private void initValue(Field field, XmlElementNode<String, Object> node) {
//        Log.e(TAG, "node --->>" + node);
        field.setAccessible(true);
        String name = field.getName();
        String key = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
//        Log.d(TAG, "initValue name--->" + name + ",key-->>" + key + "--getKey--" + node.get(key));
        try {
            Object value = field.get(this);
            if (value instanceof Integer) {
                field.set(this, node.getInt(key));
            } else if (value instanceof Long) {
                field.set(this, node.getLong(key));
            } else if (value instanceof Double) {
                field.set(this, node.getDouble(key));
            } else if (value instanceof Boolean) {
                field.set(this, node.getBoolean(key));
            } else if (value instanceof String) {
//                Log.e(TAG, "String setting.-->>" + node.getString(key));
                field.set(this, node.getString(key));
            } else if (value instanceof Float) {
                field.set(this, node.getFloat(key));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "District{" +
                "localName='" + localName + '\'' +
                ", areaNumber=" + areaNumber +
                '}';
    }
}

