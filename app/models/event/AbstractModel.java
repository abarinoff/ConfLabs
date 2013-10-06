package models.event;

import org.codehaus.jackson.annotate.JsonIgnore;
import play.db.ebean.Model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.LinkedList;
import java.util.List;

@MappedSuperclass
abstract public class AbstractModel extends Model {
    abstract public void merge(AbstractModel modelToMerge);

    @JsonIgnore
    abstract public Long getModelId();

    public <T extends AbstractModel> List<T> mergeOneToManyAssociations(List<T> mergeInto, List<T> mergeScr) {
        List<T> modelsToMerge = new LinkedList<T>(mergeScr);
        List<T> modelsToRemove = new LinkedList<T>();

        for(int i = 0; i < mergeInto.size(); i++) {
            T model = mergeInto.get(i);
            for (int j = 0; j < modelsToMerge.size(); j++) {
                T mergeModel = modelsToMerge.get(j);
                if (model.getModelId() == mergeModel.getModelId()) {
                    model.merge(mergeModel);
                    modelsToMerge.remove(j);
                    break;
                }
                if (j >= modelsToMerge.size() - 1) {
                    modelsToRemove.add(model);
                }
            }
        }
        mergeInto.removeAll(modelsToRemove);
        mergeInto.addAll(modelsToMerge);

        return modelsToRemove;
    }
}
