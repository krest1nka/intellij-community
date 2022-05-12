// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.cce

import com.intellij.cce.core.Language
import com.intellij.codeInsight.daemon.impl.quickfix.ImportClassFix
import com.intellij.codeInspection.HintAction

class JavaImportListProvider() : ImportListProvider {
  override val language: Language = Language.JAVA

  override fun getListOfImports(importHints: List<HintAction>): List<String> {
    val suggestions = mutableListOf<String>()
    for (import in importHints) {
      for (str in (import as ImportClassFix).classesToImport) {
        if (str.qualifiedName != null) suggestions.add(str.qualifiedName!!)
      }
    }
    return suggestions
  }
}