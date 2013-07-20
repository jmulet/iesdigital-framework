/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javar;

import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

/**
 *
 * @author Josep
 */
public class JRTabbedPaneBeanInfo extends SimpleBeanInfo {
    @Override
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JRTabbedPaneBeanInfo.class, null);
        desc.setValue("containerDelegate", "getContentContainer");
        return desc;
    }
}
