package com.applog;

public interface FormatStrategy {

  void log(int priority, String tag, String message);
}
