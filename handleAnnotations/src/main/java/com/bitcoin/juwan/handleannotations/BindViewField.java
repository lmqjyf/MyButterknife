package com.bitcoin.juwan.handleannotations;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;

/**
 * author: liumengqiang
 * Date : 2019/10/26
 * Description :
 */
public class BindViewField {
    Element elementField;

    int resId;

    String packageName;

    BindViewField(String packageName, Element element, int resId) {
        this.elementField =  element;
        this.packageName = packageName;
        if(resId < 0) {
            throw new IllegalArgumentException(
                    packageName + "."
                    + element.getEnclosingElement().getSimpleName()
                    + "$"
                    + element.getSimpleName() + "$BindView注释需要设置资源ID！");
        } else {
            this.resId = resId;
        }
    }

    int getResId() {
        return resId;
    }

    String getFieldName() {
        return elementField.getSimpleName().toString();
    }

    TypeMirror getTypeFieldName() {
        return this.elementField.asType();
    }
}
