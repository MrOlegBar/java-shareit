package ru.practicum.shareit.base;

public abstract class BaseModel<T> {

    protected T id;

    public BaseModel() {
    }

    public BaseModel(T id) {
        this.id = id;
    }

    public T getId() {
        return this.id;
    }

    public void setId(T id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof BaseModel)) {
            return false;
        } else {
            BaseModel<?> baseModel = (BaseModel<?>) o;
            if (!baseModel.getClass().equals(this.getClass())) {
                return false;
            } else {
                return this.id != null && this.id.equals(baseModel.id);
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}