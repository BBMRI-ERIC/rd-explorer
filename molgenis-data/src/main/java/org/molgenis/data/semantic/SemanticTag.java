package org.molgenis.data.semantic;

import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.TagMetadata;

/**
 * @param <S> subject type
 * @param <O> object type
 * @param <C> code system type
 */
public class SemanticTag<S, O, C> {
  private final S subject;
  private final Relation relation;
  private final O object;
  private final C codeSystem;
  private final String identifier;

  /** @param <S> subject type */
  public static <S> SemanticTag<S, LabeledResource, LabeledResource> asTag(
      S subjectType, Entity tagEntity) {
    String identifier = tagEntity.getString(TagMetadata.ID);
    String relationIri = tagEntity.getString(TagMetadata.RELATION_IRI);
    Relation relation = Relation.forIRI(relationIri);
    if (relation == null) {
      throw new IllegalArgumentException("Unknown relation iri [" + relationIri + "]");
    }

    LabeledResource codeSystem = null;
    if (tagEntity.getString(TagMetadata.CODE_SYSTEM) != null) {
      codeSystem =
          new LabeledResource(
              tagEntity.getString(TagMetadata.CODE_SYSTEM),
              tagEntity.getString(TagMetadata.CODE_SYSTEM));
    }

    LabeledResource objectResource =
        new LabeledResource(
            tagEntity.getString(TagMetadata.OBJECT_IRI), tagEntity.getString(TagMetadata.LABEL));

    return new SemanticTag<>(identifier, subjectType, relation, objectResource, codeSystem);
  }

  public SemanticTag(String identifier, S subject, Relation relation, O object, C codeSystem) {
    this.identifier = identifier;
    this.subject = subject;
    this.relation = relation;
    this.object = object;
    this.codeSystem = codeSystem;
  }

  public S getSubject() {
    return subject;
  }

  public Relation getRelation() {
    return relation;
  }

  public O getObject() {
    return object;
  }

  public C getCodeSystem() {
    return codeSystem;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String toString() {
    return relation.getLabel() + " " + getObject();
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((codeSystem == null) ? 0 : codeSystem.hashCode());
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
    result = prime * result + ((object == null) ? 0 : object.hashCode());
    result = prime * result + ((relation == null) ? 0 : relation.hashCode());
    result = prime * result + ((subject == null) ? 0 : subject.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SemanticTag other = (SemanticTag) obj;
    if (codeSystem == null) {
      if (other.codeSystem != null) return false;
    } else if (!codeSystem.equals(other.codeSystem)) return false;
    if (identifier == null) {
      if (other.identifier != null) return false;
    } else if (!identifier.equals(other.identifier)) return false;
    if (object == null) {
      if (other.object != null) return false;
    } else if (!object.equals(other.object)) return false;
    if (relation != other.relation) return false;
    if (subject == null) {
      if (other.subject != null) return false;
    } else if (!subject.equals(other.subject)) return false;
    return true;
  }
}
