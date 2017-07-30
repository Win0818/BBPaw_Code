LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := nineoldandroids \
							   stickylistheaders_lib \
							   android-support-v4

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := BBPaw-ECT
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := nineoldandroids:libs/nineoldandroids-2.4.0.jar \
										stickylistheaders_lib:libs/stickylistheaders_lib.jar \
										android-support-v4:libs/android-support-v4.jar
include $(BUILD_MULTI_PREBUILT)
