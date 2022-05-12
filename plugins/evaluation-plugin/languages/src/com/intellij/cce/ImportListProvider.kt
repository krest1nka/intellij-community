// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.cce

import com.intellij.cce.core.Language
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiFile

interface ImportListProvider {
  companion object {
    val EP_NAME: ExtensionPointName<ImportListProvider> = ExtensionPointName.create("com.intellij.cce.importListProvider")
  }

  val language: Language

  fun getListOfImports(psiFile: PsiFile): List<String>
}