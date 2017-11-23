package com.applog;

public interface LogStrategy {

  void log(int priority, String tag, String message);
}
