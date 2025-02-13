// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.internal.psiView;

import com.intellij.ide.highlighter.ArchiveFileType;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.ide.highlighter.WorkspaceFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

class PsiViewerSourceWrapper implements Comparable<PsiViewerSourceWrapper> {

  final FileType myFileType;
  final PsiViewerExtension myExtension;

  private PsiViewerSourceWrapper(@NotNull FileType fileType) {
    myFileType = fileType;
    myExtension = null;
  }

  private PsiViewerSourceWrapper(@NotNull PsiViewerExtension extension) {
    myFileType = null;
    myExtension = extension;
  }

  public String getText() {
    return myFileType != null ? myFileType.getName() + " file" : myExtension.getName();
  }

  @Nullable
  public Icon getIcon() {
    return myFileType != null ? myFileType.getIcon() : myExtension.getIcon();
  }

  @Override
  public int compareTo(@NotNull PsiViewerSourceWrapper o) {
    return getText().compareToIgnoreCase(o.getText());
  }


  @NotNull
  static List<PsiViewerSourceWrapper> getExtensionBasedWrappers() {
    return ContainerUtil.map(PsiViewerExtension.EP_NAME.getExtensionList(), el -> new PsiViewerSourceWrapper(el));
  }

  @NotNull
  static List<PsiViewerSourceWrapper> getFileTypeBasedWrappers() {
    Set<FileType> allFileTypes = new HashSet<>();
    List<PsiViewerSourceWrapper> sourceWrappers = new ArrayList<>();
    Collections.addAll(allFileTypes, FileTypeManager.getInstance().getRegisteredFileTypes());
    for (Language language : Language.getRegisteredLanguages()) {
      FileType fileType = language.getAssociatedFileType();
      if (fileType != null) {
        allFileTypes.add(fileType);
      }
    }
    for (FileType fileType : allFileTypes) {
      if (isAcceptableFileType(fileType)) {
        PsiViewerSourceWrapper wrapper = new PsiViewerSourceWrapper(fileType);
        sourceWrappers.add(wrapper);
      }
    }

    return sourceWrappers;
  }

  private static boolean isAcceptableFileType(FileType fileType) {
    return fileType != StdFileTypes.GUI_DESIGNER_FORM &&
           fileType != ModuleFileType.INSTANCE &&
           fileType != ProjectFileType.INSTANCE &&
           fileType != WorkspaceFileType.INSTANCE &&
           fileType != ArchiveFileType.INSTANCE &&
           fileType != FileTypes.UNKNOWN &&
           fileType != FileTypes.PLAIN_TEXT &&
           !(fileType instanceof AbstractFileType) &&
           !fileType.isBinary() &&
           !fileType.isReadOnly();
  }
}
