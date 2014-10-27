package awqatty.b.DrawMath.LinkedParameters;

import java.util.ArrayList;
import java.util.List;


public class LinkedRect {
	
	private final List<LinkedParams<Integer>> dims;
	public static final int LEFT	= 0;
	public static final int RIGHT	= 1;
	public static final int WIDTH	= 2;
	
	public static final int TOP		= 3;
	public static final int BOTTOM	= 4;
	public static final int HEIGHT	= 5;

	public static final int param_count	= 6;
	
	
	public LinkedRect() {
		dims = new ArrayList<LinkedParams<Integer>>(param_count);
		
		for (int i=0; i<param_count; ++i)
			dims.add(new LinkedParams<Integer>());
		
		final List<Float> factors = new ArrayList<Float>(3);
		//	left + width - right = 0
		factors.set(LEFT,(float)1);
		factors.set(WIDTH,(float)1);
		factors.set(RIGHT,(float)-1);
		
		List<LinkedParams<Integer>> sublist;
		ParamRelationForm<Integer> relation;

		sublist = dims.subList(LEFT, WIDTH+1);
		relation = new OmniParamLinearRelationI(sublist,factors);
		for (LinkedParams<Integer> param : sublist)
			param.addRelation(relation);
		
		sublist = dims.subList(TOP, HEIGHT+1);
		relation = new OmniParamLinearRelationI(sublist,factors);
		for (LinkedParams<Integer> param : sublist)
			param.addRelation(relation);
	}

}
