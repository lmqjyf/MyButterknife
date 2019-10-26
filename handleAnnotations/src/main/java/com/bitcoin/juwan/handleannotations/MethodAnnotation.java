package com.bitcoin.juwan.handleannotations;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;

/**
 * author: liumengqiang
 * Date : 2019/10/26
 * Description : 使用onClick注解的方法
 */
public class MethodAnnotation {
    ExecutableElement executableElement;

    List<Integer> resIdList;

    MethodAnnotation(ExecutableElement executableElement, int []resIdArray) {
        this.executableElement = executableElement;
        resIdList = new ArrayList<>();
        handleResIdArray(resIdArray);
    }

    private void handleResIdArray(int[] resIdArray) {
        for(int resId : resIdArray) {
            resIdList.add(resId);
        }
    }

    String getAnnotationMethodName() {
        return executableElement.getSimpleName().toString();
    }

    List<Integer> getNeedSetClickResId() {
        return resIdList;
    }


    boolean resIdIsNeedClick(int resId) {
        return resIdList.contains(resId);
    }
}
