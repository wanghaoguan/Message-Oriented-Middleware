
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
 *         &lt;element name="APP_PushNotification_OneNotice_ToMidResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "appPushNotificationOneNoticeToMidResult"
})
@XmlRootElement(name = "APP_PushNotification_OneNotice_ToMidResponse")
public class APPPushNotificationOneNoticeToMidResponse {

    @XmlElement(name = "APP_PushNotification_OneNotice_ToMidResult")
    protected String appPushNotificationOneNoticeToMidResult;

    /**
     * Gets the value of the appPushNotificationOneNoticeToMidResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAPPPushNotificationOneNoticeToMidResult() {
        return appPushNotificationOneNoticeToMidResult;
    }

    /**
     * Sets the value of the appPushNotificationOneNoticeToMidResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAPPPushNotificationOneNoticeToMidResult(String value) {
        this.appPushNotificationOneNoticeToMidResult = value;
    }

}
