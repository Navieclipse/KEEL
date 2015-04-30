package keel.Algorithms.DiscretizeFramework.DiscretizeAttribute.impl;

import keel.Algorithms.DiscretizeFramework.DiscretizeAttribute.DiscretizeAttribute;
import keel.Algorithms.DiscretizeFramework.Utils.Common;

public class DiscretizeAttributeImpl implements DiscretizeAttribute{
	public void discretizeAttribute(int attribute,int []values,int begin,int end){
		//第一步，初始化断点
		Common.initSet.init(attribute, values, begin, end);
		
		//第二步，调整断点
		Common.changeResult.changeResult(attribute, values, begin, end);
	}
}
