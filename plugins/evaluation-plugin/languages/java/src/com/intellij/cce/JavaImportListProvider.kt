// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.cce

import com.intellij.cce.core.Language
import com.intellij.codeInsight.daemon.impl.quickfix.ImportClassFix
import com.intellij.codeInspection.HintAction
import kotlin.streams.toList

class JavaImportListProvider() : ImportListProvider {
  override val language: Language = Language.JAVA

  override fun getListOfImports(importHints: List<HintAction>): List<String> {
    //return listOf("kek", "lol")
    var kek = importHints.stream().map{ hint -> hint as ImportClassFix }
      .flatMap{ hint ->  hint.classesToImport.stream()}.map { toString() }.toList()
    return kek
  }
}