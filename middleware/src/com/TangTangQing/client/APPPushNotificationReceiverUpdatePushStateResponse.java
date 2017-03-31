
package com.TangTangQing.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="APP_PushNotification_Receiver_UpdatePushStateResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "appPushNotificationReceiverUpdatePushStateResult"
})
@XmlRootElement(name = "APP_PushNotification_Receiver_UpdatePushStateResponse")
public class APPPushNotificationReceiverUpdatePushStateResponse {

    @XmlElement(name = "APP_PushNotification_Receiver_UpdatePushStateResult")
    protected int appPushNotificationReceiverUpdatePushStateResult;

    /**
     * Gets the value of the appPushNotificationReceiverUpdatePushStateResult property.
     * 
     */
    public int getAPPPushNotificationReceiverUpdatePushStateResult() {
        return appPushNotificationReceiverUpdatePushStateResult;
    }

    /**
     * Sets the value of the appPushNotificationReceiverUpdatePushStateResult property.
     * 
     */
    public void setAPPPushNotificationReceiverUpdatePushStateResult(int value) {
        this.appPushNotificationReceiverUpdatePushStateResult = value;
    }

}
