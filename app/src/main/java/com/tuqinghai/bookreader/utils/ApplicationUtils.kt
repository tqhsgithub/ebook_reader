package com.tuqinghai.bookreader.utils

import android.app.Application
import java.lang.reflect.AccessibleObject.setAccessible
import java.lang.reflect.InvocationTargetException


object ApplicationUtils {

    val context:Application by lazy {
        var application: Application? = null
        val activityThreadClass: Class<*>
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread")
            val appField = activityThreadClass.getDeclaredField("mInitialApplication")
            // Object object = activityThreadClass.newInstance();
            val method = activityThreadClass.getMethod("currentActivityThread")
            // 得到当前的ActivityThread对象
            val localObject = method.invoke(null)
            appField.isAccessible = true
            application = appField.get(localObject) as Application
            // appField.
        } catch (e: ClassNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        application!!

    }


}