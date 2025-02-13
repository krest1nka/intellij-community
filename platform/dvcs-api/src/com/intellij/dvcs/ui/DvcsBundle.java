// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.dvcs.ui;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

public final class DvcsBundle extends DynamicBundle {
  @NonNls static final String BUNDLE = "messages.DvcsBundle";
  private static final DvcsBundle INSTANCE = new DvcsBundle();

  private DvcsBundle() { super(BUNDLE); }

  @NotNull
  public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
    return INSTANCE.getMessage(key, params);
  }

  @NotNull
  public static Supplier<@Nls String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
    return INSTANCE.getLazyMessage(key, params);
  }

  /**
   * @deprecated prefer {@link #message(String, Object...)} instead
   */
  @NotNull
  @Deprecated(forRemoval = true)
  public static @Nls String getString(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key) {
    return message(key);
  }
}
