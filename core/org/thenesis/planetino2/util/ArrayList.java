/**************************************************************************
 * Copyright (c) 2001 by Punch Telematix. All rights reserved.             *
 *                                                                         *
 * Redistribution and use in source and binary forms, with or without      *
 * modification, are permitted provided that the following conditions      *
 * are met:                                                                *
 * 1. Redistributions of source code must retain the above copyright       *
 *    notice, this list of conditions and the following disclaimer.        *
 * 2. Redistributions in binary form must reproduce the above copyright    *
 *    notice, this list of conditions and the following disclaimer in the  *
 *    documentation and/or other materials provided with the distribution. *
 * 3. Neither the name of Punch Telematix nor the names of                 *
 *    other contributors may be used to endorse or promote products        *
 *    derived from this software without specific prior written permission.*
 *                                                                         *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED          *
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF    *
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.    *
 * IN NO EVENT SHALL PUNCH TELEMATIX OR OTHER CONTRIBUTORS BE LIABLE       *
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR            *
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF    *
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR         *
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,   *
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE    *
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN  *
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                           *
 **************************************************************************/

/*
** $Id: Vector.java,v 1.2 2006/04/18 11:35:28 cvs Exp $
*/

package org.thenesis.planetino2.util;

import java.util.Enumeration;

public class ArrayList {

	protected transient int modCount = 0;
	protected Object[] elementData;
	protected int elementCount;
	protected int capacityIncrement;

	private transient int initialCapacity;

	public ArrayList(int initCap, int capIncr) {
		initialCapacity = initCap;
		elementData = new Object[initCap];
		capacityIncrement = capIncr;
	}

	public ArrayList(int initCap) {
		initialCapacity = initCap;
		elementData = new Object[initCap];
	}

	public ArrayList() {
		initialCapacity = 10;
		elementData = new Object[10];
	}

	//  public Vector(Collection c) {
	//    initialCapacity = c.size();
	//    elementData = new Object[initialCapacity];
	//    Iterator it = c.iterator();
	//    while (it.hasNext()) { 
	//      elementData[elementCount++] = it.next();
	//    }
	//  }

	/**
	 * calls addAll from AbstractList
	 * 
	 */
	//  public  boolean addAll(Collection c) {
	//  	super.addAll(c);
	//  	return (c.size()>0);
	//  }
	//
	//
	//  public  boolean addAll(int idx, Collection c) {
	//  	super.addAll(idx,c);
	//  	return (c.size()>0);
	//  }

	//  public  Object[] toArray() {
	//  	return toArray(new Object[elementCount]);
	//  }
	//
	//  public  Object[] toArray(Object [] arr) {
	//        Object [] array = arr;
	//        if ( array.length < elementCount ) array = (Object[])Array.newInstance(arr.getClass().getComponentType(),elementCount);
	//        if ( array.length > elementCount ) array[elementCount] = null ;
	//        System.arraycopy(elementData,0,array,0,elementCount);
	//        return  array;
	//
	//  }

	public String toString() {
		StringBuffer buffer = new StringBuffer("[");

		for (int i = 0; i < elementCount; i++) {
			Object element = elementData[i];
			buffer.append(element == this ? "(this Collection)" : String.valueOf(element)).append(", ");

		}
		int length = buffer.length();
		if (length > 1) {
			buffer.setLength(length - 2);
		}
		buffer.append(']');
		return buffer.toString();
	}

	//  public  Object clone() {
	//    Vector nieuw = null;
	//    try {
	//    	nieuw = (Vector) super.clone();
	//    }
	//    catch(CloneNotSupportedException cnse) {}
	//    nieuw.elementData = new Object[elementData.length];
	//    System.arraycopy(this.elementData, 0, nieuw.elementData, 0, this.elementCount); 	
	//    return nieuw;
	//  }

	public Object elementAt(int index) {
		if (index < elementCount) {
			return elementData[index];
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	public void setElementAt(Object obj, int index) throws ArrayIndexOutOfBoundsException {
		// throw exception if  0 > index  or index >= elementCount
		if (index < 0 || index >= elementCount)
			throw new ArrayIndexOutOfBoundsException();
		elementData[index] = obj;
	}

	//  public Object firstElement() throws NoSuchElementException {
	//    (this) {
	//      if(elementCount==0) throw new NoSuchElementException();
	//
	//      return elementData[0];
	//    }
	//  }
	//  
	//  public Object lastElement() throws NoSuchElementException {
	//    (this) {
	//      if (elementCount==0) throw new NoSuchElementException();
	//
	//      return elementData[elementCount - 1];
	//    }
	//  }

	public void addElement(Object obj) {
		if (elementCount == elementData.length) {
			ensureCapacity(elementCount + 1);
		}
		elementData[elementCount++] = obj;
		modCount++;
	}

	public void insertElementAt(Object obj, int index) throws ArrayIndexOutOfBoundsException {
		int elementCount = this.elementCount;
		Object[] elementData = this.elementData;

		if (index < 0 || index > elementCount)
			throw new ArrayIndexOutOfBoundsException();

		if (elementCount >= elementData.length) {
			ensureCapacity(elementCount + 1);
			elementData = this.elementData;
		}
		if (index < elementCount) {
			System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
		}

		elementData[index] = obj;
		this.elementCount++;
		modCount++;
	}

	public boolean removeElement(Object obj) {
		int i;

		i = indexOf(obj, 0);

		if (i < 0)

			return false;

		removeElement(i);
		return true;
	}

	// When removing an element we dont automatically trim the array:
	// chances are another element will be added soon.

	public void removeElementAt(int index) throws ArrayIndexOutOfBoundsException {
		if (index < 0 || index >= elementCount)
			throw new ArrayIndexOutOfBoundsException();

		if (index < elementCount - 1) {
			System.arraycopy(elementData, index + 1, elementData, index, elementCount - index - 1);
		}
		modCount++;
		elementData[--elementCount] = null;
	}

	public boolean isEmpty() {
		return elementCount == 0;
	}

	public int size() {
		return elementCount;
	}

	public void setSize(int newSize) throws ArrayIndexOutOfBoundsException {
		if (newSize < 0)
			throw new ArrayIndexOutOfBoundsException();
		ensureCapacity(newSize);
		if (newSize != elementCount)
			modCount++;
		if ((this.capacity() >= newSize) && (elementData.length >= newSize)) {
			while (elementCount < newSize)
				elementData[elementCount++] = null;
			while (elementCount > newSize)
				elementData[--elementCount] = null;
		}
		trimToSize(); //capacity doesn't need to change
	}

	public int capacity() {
		return elementData.length;
	}

	public void ensureCapacity(int minCapacity) {
		if (elementData.length < minCapacity) {
			int newCapacity;

			if (capacityIncrement > 0) {
				newCapacity = elementData.length + capacityIncrement;
			} else {
				newCapacity = elementData.length * 2;
			}

			if (newCapacity < minCapacity)
				newCapacity = minCapacity;

			Object[] newArray = new Object[newCapacity];
			System.arraycopy(elementData, 0, newArray, 0, elementCount);

			elementData = newArray;
		}
	}

	public void trimToSize() {
		int newCapacity;

		if (elementData.length > elementCount) {
			newCapacity = elementCount;

			if (newCapacity < initialCapacity && initialCapacity < elementData.length) {
				newCapacity = initialCapacity;
			}

			Object[] newArray = new Object[newCapacity];
			System.arraycopy(elementData, 0, newArray, 0, elementCount);

			elementData = newArray;
		}
	}

	public void copyInto(Object anArray[]) throws ArrayIndexOutOfBoundsException {
		System.arraycopy(elementData, 0, anArray, 0, elementCount);
	}

	//  public Iterator iterator() {
	//    return new VectorEnum();
	//  }

	public boolean contains(Object elem) {
		return indexOf(elem, 0) >= 0;
	}

	public int indexOf(Object elem) {
		int i;

		if (elem != null) {
			for (i = 0; i < elementCount; ++i) {
				if (elem.equals(elementData[i]))

					return i;

			}
		} else {
			for (i = 0; i < elementCount; ++i) {
				if (elementData[i] == null)
					return i;
			}
		}
		return -1;

	}

	public int indexOf(Object elem, int index) throws ArrayIndexOutOfBoundsException {
		int i;
		if (elem != null) {
			for (i = index; i < elementCount; ++i) {
				if (elem.equals(elementData[i]))

					return i;

			}
		} else {
			for (i = index; i < elementCount; ++i) {
				if (elementData[i] == null)
					return i;
			}
		}
		return -1;

	}

	public int lastIndexOf(Object elem) {
		int i;
		if (elem != null) {
			for (i = elementCount - 1; i >= 0; --i) {
				if (elem.equals(elementData[i]))

					return i;
			}
		} else {
			for (i = elementCount - 1; i >= 0; --i) {
				if (elementData[i] == null)
					return i;
			}
		}

		return -1;

	}

	public int lastIndexOf(Object elem, int index) throws ArrayIndexOutOfBoundsException {
		int i;

		if (elem != null) {
			for (i = index; i >= 0; --i) {
				if (elem.equals(elementData[i]))

					return i;
			}
		} else {
			for (i = index; i >= 0; --i) {
				if (elementData[i] == null)
					return i;
			}
		}

		return -1;

	}

	/**
	** Iterator is inherited from AbstractList !!!
	** --> it is allowed to overwrite it!
	*/
	public Iterator iterator() {
		return new VectorIterator();
	}

	public Enumeration elements() {
		return new VectorEnum();
	}

	protected void removeRange(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex > elementCount || fromIndex > toIndex)
			throw new ArrayIndexOutOfBoundsException();
		System.arraycopy(elementData, toIndex, elementData, fromIndex, elementCount - toIndex);
		int j = elementCount;
		elementCount -= toIndex - fromIndex;
		for (int i = elementCount; i < j; i++) {
			elementData[i] = null;
		}
		modCount++;

	}

	/*
	* implemented
	*
	*/
	public Object set(int index, Object element) {
		if (index < 0 || index >= elementCount)
			throw new ArrayIndexOutOfBoundsException();
		Object old = elementData[index];
		elementData[index] = element;
		return old;
	}

	///**
	//* is implemented
	//* this method calls subList in AbstractList
	//*/
	//  public List subList(int fromIndex, int toIndex) throws UnsupportedOperationException {
	////    throw new UnsupportedOperationException("subList in Class Vector is not supported");
	//  	return super.subList(fromIndex,toIndex);
	//
	//  }
	//
	//  public  boolean containsAll(Collection c) {
	//    Iterator it=c.iterator();
	//    while (it.hasNext()) {
	//      if ( !contains(it.next()) )
	//        return false;
	//    }
	//    return true;
	//  }
	//
	///*
	//* is supported ...
	//*/
	//  public  boolean removeAll(Collection c) throws UnsupportedOperationException {
	////    throw new UnsupportedOperationException("removeAll in Class Vector is not supported");
	//      if ( c.isEmpty() ) return false; // nothing to remove
	//      Object [] newElements = new Object[elementData.length];
	//      int newElementCount=0;
	//      for (int i=0 ; i < elementCount ; i++) {
	//      		if (!c.contains(elementData[i])) {
	//      		 	newElements[newElementCount++]=elementData[i];
	//      		}
	//      }
	//      if (elementCount == newElementCount) return false;
	//      elementData = newElements;
	//      elementCount = newElementCount;
	//      modCount++;
	//      return true;
	//	
	//
	//  }
	///*
	//* is supported ...
	//*/
	//  public  boolean retainAll(Collection c) throws UnsupportedOperationException {
	////    throw new UnsupportedOperationException("retainAll in Class Vector is not supported");
	//
	//      if ( c.isEmpty() ) {
	//      boolean returnboolean = elementCount > 0;
	//      clear();
	//      return returnboolean;
	//      }
	//      Object [] newElements = new Object[elementData.length];
	//      int newElementCount=0;
	//      for (int i=0 ; i < elementCount ; i++) {
	//      		if (c.contains(elementData[i])) {
	//      		 	newElements[newElementCount++]=elementData[i];
	//      		}
	//      }
	//      if (elementCount == newElementCount) return false;
	//      elementData = newElements;
	//      elementCount = newElementCount;
	//      modCount++;
	//      return true;
	//
	//  }

	public void clear() {
		for (int i = 0; i < elementCount; i++) {
			elementData[i] = null;
		}
		if (elementCount > 0)
			modCount++;
		elementCount = 0;
	}

	/**
	 * Object.hashCode is overwritten see Algorithm p 965 of the Java Class
	 * Library second edition, volume 1 supplement for the java 2 Platform v1.2
	 */
	public int hashCode() {
		int hashcode = 1;
		int count = elementCount;
		Object data[] = elementData;
		for (int i = 0; i < count; i++) {
			hashcode = 31 * hashcode + (data[i] == null ? 0 : data[i].hashCode());
		}
		return hashcode;
	}

	/**
	 * equals overwrites Object.equals
	 * 
	 */
	public boolean equals(Object o) {
		if (!(o instanceof ArrayList))
			return false;
		ArrayList list = (ArrayList) o;
		if (list.size() != elementCount)
			return false;
		for (int i = 0; i < elementCount; i++) {
			if (!(elementData[i] == null ? list.elementAt(i) == null : elementData[i].equals(list.elementAt(i))))
				return false;
		}
		return true;
	}

	// This could probably be done with an anonymous class, but for now:

	private class VectorEnum implements Enumeration {
		private int _i = 0;

		public boolean hasMoreElements() {
			return (_i < elementCount);
		}

		public Object nextElement() throws NoSuchElementException {
			if (_i >= elementCount) {
				throw new NoSuchElementException();
			}
			return elementData[_i++];
		}
	}

	private class VectorIterator implements Iterator {
		private int i = 0;
		private int Status = 0;
		private int localModCount = modCount;

		public boolean hasNext() throws ConcurrentModificationException {
			return (i < elementCount);
		}

		public Object next() throws ConcurrentModificationException, NoSuchElementException {
			if (localModCount != modCount)
				throw new ConcurrentModificationException("in the Iterator from Vector method next");
			if (i < elementCount) {
				Status = 1;
				return elementData[i++];
			} else
				throw new NoSuchElementException();
		}

		public void remove() throws ConcurrentModificationException, IllegalStateException {
			if (localModCount != modCount)
				throw new ConcurrentModificationException("in the Iterator from Vector method remove");
			if (Status != 1)
				throw new IllegalStateException("remove() must be called after next()");
			ArrayList.this.removeElement(--i);
			Status = 0;
			localModCount++;

		}
	}
}
