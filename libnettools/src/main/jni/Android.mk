LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := com_github_xgfjyw_libnettools_NetSelect.c
LOCAL_MODULE := libnettools

LOCAL_LDLIBS := -llog -lm -lc

include $(BUILD_SHARED_LIBRARY)
