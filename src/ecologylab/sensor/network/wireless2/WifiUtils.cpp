#include "WifiUtils.h"
#include <Wlanapi.h>
static HANDLE wifiHandle;

typedef struct {
  JavaVM *jvm;
  jclass wifiUtils;
} JavaContext;

JNIEXPORT void JNICALL Java_WifiUtils_initialize
  (JNIEnv *env, jclass wifiUtils); {
    
    JavaContext *context = new JavaContext;
    DWORD ret;
    DWORd negotiatedVersion;
 
    //setup global variables for access by callbacks
    env->GetJavaVM(&(context->jvm));
    context->wifiUtils = wifiUtils;

    ret = WlanOpenHandle(1, NULL,
                         &negotiatedVersion,&wifiHandle);
                         
    if(ret != ERROR_SUCCESS)
    {
      fprintf(stderr, "WifiUtils(Native): Couldn't initialize a handle!\n");
    } else {
      //register notifications
      ret = WlanRegisterNotification(wifiHandle, WLAN_NOTIFICATION_SOURCE_ALL,
                                     true, connectCallBack, (void*)context,
                                     NULL, NULL);
      if (ret != ERROR_SUCCESS)
      {
        fprintf(stderr, "WifiUtils(Native): Couldn't register notifications!\n");
      }
    }
   
    env->ReleaseStringUTFChars(jInputString, InputString);
}

JNIEXPORT void JNICALL Java_WifiUtils_updateStats
  (JNIEnv *env, jclass cls) {
  
  WLAN_INTERFACE_INFO_LIST *intfList;
  DWORD ret = WlanEnumInterfaces(wifiHandle, NULL, &intfList);
  jfieldID = interfaceStateFid, ssidFid, wlanSignalQualityFid, bssidFid;
  
  
  if (ret != ERROR_SUCCESS)
  {
    fprintf(stderr, "WifiUtils(Native): Failed to enumerate interfaces!\n");
  }
  
  /**
        * Load fields that will be accessed by the update process.
        **/
  interfaceStateFid = env->GetFieldID(cls, "interfaceState", "I");
  if(interfaceStateFid == 0)
  {
    fprintf(stderr, "WifiUtils(Native): couldn't find interfaceState field!\n");
    return;
  }
  
  ssidFid = env->GetFieldID(cls, "ssid", "Ljava/lang/String;");
  if(ssidFid == 0)
  {
    fprintf(stderr, "WifiUtils(Native): couldn't get ssid field!\n");
    return;
  }
  
  wlanSignalQualityFid = env->GetFieldID(cls, "wlanSignalQuality", "I");
  if(interfaceStateFid == 0)
  {
    fprintf(stderr, "WifiUtils(Native): couldn't find wlanSignalQuality field!\n");
    return;
  }
  
  bssidFid = env->GetFieldID(cls, "bssid", "Ljava/lang/String;");
  if(interfaceStateFid == 0)
  {
    fprintf(stderr, "WifiUtils(Native): couldn't find bssid field!\n");
    return;
  }
  
  for(intfList->dwIndex = 0; 
      intfList->dwIndex < intfList->dwNumberOfItems;
      intfList->dwIndex++)
  {
    if(intfList->InterfaceInfo[intfList->dwIndex].isState !=
       wlan_interface_state_disconnected)
    {
      env->SetStaticIntField(cls, interfaceStateFid,
                             intfList->InterfaceInfo[intfList->dwIndex].isState);
      break;
    }
  }
  if(intfList->dwIndex == intfList->dwNumberOfItems) { //no connected interface
    env->SetStaticIntField(cls, interfaceStateFid,
                           wlan_interface_state_disconnected);
  } else {
    GUID *activeIntfGuid = &(intfList->InterfaceInfo[intfList->dwIndex].InterfaceGuid);
    DWORD dataSize;
    WLAN_CONNECTION_ATTRIBUTES *attrs;
    ret = WlanQueryInterface(wifiHandle, activeIntfGuid,
                             wlan_intf_opcode_current_connection,
                             NULL, &dataSize, &attrs, NULL);
    
    if(ret != ERROR_SUCCESS)
    {
      fprintf(stderr, "WifiUtils(Native): failed to query the active interface!\n");
      WLanFreeMemory(intfList);
      return;
    }
    
    /*get and set ssid*/
    UCHAR ssidBuf[DOT11_SSID_MAX_LENGTH + 1];
    jstring ssid;
    memcpy(ssidBuf, attrs->wlanAssociationAttributes.dot11Ssid.ucSSID,
           attrs->wlanAssociationAttributes.dot11Ssid.uSSIDLength);
    ssidBuf[attrs->wlanAssociationAttributes.dot11Ssid.uSSIDLength] = 0;
    
    env->SetStaticObjectField(cls, ssidFid, env->NewStringUTF(env, ssidBuf));
    
    /*get and set signal quality*/
    env->SetStaticIntField(cls, wlanSignalQualityFid,
                           attrs->wlanAssociationAttributes.wlanSignalQuality);
    
    /*get and set  bssid*/
    UCHAR *mac = attrs->wlanAssociationAttributes.dot11Bssid;
    snprintf(ssidBuf, DOT11_SSID_MAX_LENGTH + 1,
             "%02x:%02x:%02x:%02x:%02x:%02x",
             mac[0],mac[1],mac[2],mac[3],mac[4],mac[5]);
    env->SetStaticObjectField(cls, bssidFid, env->NewStringUTF(env, ssidBuf));
    
    WLanFreeMemory(attrs);
  }
  
  WLanFreeMemory(intfList);
}
void connectedCallBack(WLAN_NOTIFICATION_DATA *data, void* con)
{
  JavaContext *context = (JavaContext*) con;
  jmethodID mid;
  jint result;
  JNIEnv *env;
  
  result = context->jvm->AttachCurrentThread(&env,NULL);
  
  if(result < 0)
  {
    fprintf(stderr, "WifiUtils(Native): Failed to attach callback thread.\n");
    context->jvm->DetachCurrentThread();
    return;
  }
  
  if(data->NotificationCode == wlan_notification_acm_connection_complete)
  {
    mid = env->GetMethodID(context->wifiUtils, 
                           "connectCallBack", "()V");
    if(mid == 0) {
      fprintf(stderr,"WifiUtils(Native): Can't find method connectCallBack.\n");
      context->jvm->DetachCurrentThread();
      return;
    }
  } else {
    mid = env->GetMethodID(context->wifiUtils, 
                           "disconnectCallBack", "()V");
    if(mid == 0) {
      fprintf(stderr,"WifiUtils(Native): Can't find method connectCallBack.\n");
      context->jvm->DetachCurrentThread();
      return;
    }
  }
  
  env->ExceptionClear();
  env->CallStaticVoidMethod(context->wifiUtils, mid);
  if(env->ExceptionOccured()) {
    fprintf(stderr, "WifiUtils(Native): Error during callback from callback.\n");
    env->ExceptionDescribe();
    env->ExceptionClear();
  }
  
  context->jvm->DetachCurrentThread();
  return;
}
