/*
 * Copyright (C) 2019 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dagger.internal.codegen.kotlin;

import static com.google.auto.common.AnnotationMirrors.getAnnotatedAnnotations;
import static com.google.auto.common.MoreElements.isAnnotationPresent;
import static dagger.internal.codegen.langmodel.DaggerElements.closestEnclosingTypeElement;

import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import javax.inject.Inject;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import kotlin.Metadata;

/** Utility class for interacting with Kotlin Metadata. */
public final class KotlinMetadataUtil {

  private final KotlinMetadataFactory metadataFactory;

  @Inject
  KotlinMetadataUtil(KotlinMetadataFactory metadataFactory) {
    this.metadataFactory = metadataFactory;
  }

  /**
   * Returns true if this element has the Kotlin Metadata annotation or if it is enclosed in an
   * element that does.
   */
  public boolean hasMetadata(Element element) {
    return isAnnotationPresent(closestEnclosingTypeElement(element), Metadata.class);
  }

  /**
   * Returns the synthetic annotations of a Kotlin property.
   *
   * <p>Note that this method only looks for additional annotations in the synthetic property
   * method, if any, of a Kotlin property and not for annotations in its backing field.
   */
  public ImmutableSet<? extends AnnotationMirror> getSyntheticPropertyAnnotations(
      VariableElement fieldElement, Class<? extends Annotation> annotationType) {
    return metadataFactory
        .create(fieldElement)
        .flatMap(metadata -> metadata.getSyntheticAnnotationMethod(fieldElement))
        .map(methodElement -> getAnnotatedAnnotations(methodElement, annotationType))
        .orElse(ImmutableSet.of());
  }

  /** Returns true if this type element is a Kotlin Object. */
  public boolean isObjectClass(TypeElement typeElement) {
    return metadataFactory.create(typeElement).map(KotlinMetadata::isObjectClass).orElse(false);
  }
}
