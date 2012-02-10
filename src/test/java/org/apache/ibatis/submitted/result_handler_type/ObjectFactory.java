package org.apache.ibatis.submitted.result_handler_type;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.ibatis.reflection.ReflectionException;

public class ObjectFactory implements org.apache.ibatis.reflection.factory.ObjectFactory {

  private static final long serialVersionUID = -8855120656740914948L;

  public <T> T create(Class<T> type) {
    return create(type, null, null);
  }

  public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    Class<?> classToCreate = resolveInterface(type);
    @SuppressWarnings( "unchecked" ) // we know types are assignable
    T created = (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
    return created;
  }

  public void setProperties(Properties properties) {
    // no props for default
  }

  private <T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    try {
      Constructor<T> constructor;
      if (constructorArgTypes == null || constructorArgs == null) {
        constructor = type.getDeclaredConstructor();
        if (!constructor.isAccessible()) {
          constructor.setAccessible(true);
        }
        return constructor.newInstance();
      }
      constructor = type.getDeclaredConstructor(constructorArgTypes.toArray(new Class[constructorArgTypes.size()]));
      if (!constructor.isAccessible()) {
        constructor.setAccessible(true);
      }
      return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
    } catch (Exception e) {
      StringBuilder argTypes = new StringBuilder();
      if (constructorArgTypes != null) {
        for (Class<?> argType : constructorArgTypes) {
          argTypes.append(argType.getSimpleName());
          argTypes.append(",");
        }
      }
      StringBuilder argValues = new StringBuilder();
      if (constructorArgs != null) {
        for (Object argValue : constructorArgs) {
          argValues.append(String.valueOf(argValue));
          argValues.append(",");
        }
      }
      throw new ReflectionException("Error instantiating " + type + " with invalid types (" + argTypes + ") or values (" + argValues + "). Cause: " + e, e);
    }
  }


  private Class<?> resolveInterface(Class<?> type) {
    Class<?> classToCreate;
    if (type == List.class || type == Collection.class) {
      classToCreate = LinkedList.class;
    } else if (type == Map.class) {
      classToCreate = LinkedHashMap.class;
    } else if (type == SortedSet.class) { // issue #510 Collections Support
      classToCreate = TreeSet.class;
    } else if (type == Set.class) {
      classToCreate = HashSet.class;
    } else {
      classToCreate = type;
    }
    return classToCreate;
  }


  public boolean isCollection(Class<?> type) {
    return Collection.class.isAssignableFrom(type);
  }

  @SuppressWarnings("unchecked")
  public void add(Object collection, Object element) {
    ((Collection<Object>) collection).add(element);
  }

  @SuppressWarnings("unchecked")
  public <E> void addAll(Object collection, List<E> elements) {
    ((Collection<Object>) collection).addAll(elements);
  }

  @SuppressWarnings("unchecked")
  public <T> T[] createArray(Class<T> type, int size) {
    return (T[]) Array.newInstance(type, size);
  }

}