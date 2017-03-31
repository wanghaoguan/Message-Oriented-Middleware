
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
 *         &lt;element name="APP_PushNotification_Receiver_ToMidResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "appPushNotificationReceiverToMidResult"
})
@XmlRootElement(name = "APP_PushNotification_Receiver_ToMidResponse")
public class APPPushNotificationReceiverToMidResponse {

    @XmlElement(name = "APP_PushNotification_Receiver_ToMidResult")
    protected String appPushNotificationReceiverToMidResult;

    /**
     * Gets the value of the appPushNotificationReceiverToMidResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAPPPushNotificationReceiverToMidResult() {
        return appPushNotificationReceiverToMidResult;
    }

    /**
     * Sets the value of the appPushNotificationReceiverToMidResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAPPPushNotificationReceiverToMidResult(String value) {
        this.appPushNotificationReceiverToMidResult = value;
    }

}
