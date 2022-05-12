// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.cce

import com.intellij.cce.core.Language
import com.intellij.codeInsight.daemon.impl.ShowAutoImportPass
import com.intellij.codeInsight.daemon.impl.quickfix.ImportClassFix
import com.intellij.psi.PsiFile

class JavaImportListProvider() : ImportListProvider {
  override val language: Language = Language.JAVA

  override fun getListOfImports(psiFile: PsiFile): List<String> {
    val importHints = psiFile.let { ShowAutoImportPass.getImportHints(it) }
    val suggestions = mutableListOf<String>()
    for (import in importHints) {
      for (str in (import as ImportClassFix).classesToImport) {
        if (str.qualifiedName != null) suggestions.add(str.qualifiedName!!)
      }
    }
    return suggestions.distinct()
  }
}