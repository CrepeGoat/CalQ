package awqatty.b.DrawMath.DrawSubTree;

import java.util.ArrayList;
import java.util.List;

import awqatty.b.DrawMath.DrawSubTree.Builders.AlignSeriesBuilder;
import awqatty.b.DrawMath.DrawToCanvas.DrawText;
import awqatty.b.GenericTextPresentation.NumberStringConverter;
import awqatty.b.ListTree.ListTree;

public final class AlignNumberSegments extends AlignAxisBase {
	
	private static float spacing;
	public static void setSpacing(float space) {
		spacing = space;
	}
	
	private final List<AlignForm> comps = new ArrayList<AlignForm>();
	
	
	/**************************************************
	 * Draw Numbers in Grouped Scale
	 */
	public static AlignForm buildDrawNumber(double number) {
		List<DrawText> seriesSigFig = new ArrayList<DrawText>();
		List<DrawText> seriesMag = new ArrayList<DrawText>();
		getNumberDraw(number, seriesSigFig, seriesMag);
		
		if (seriesMag.isEmpty()) {
			if (seriesSigFig.size() == 1)
				return seriesSigFig.get(0);
			else return new AlignNumberSegments(seriesSigFig);
		}
		else return new AlignNumberSegments(seriesSigFig,seriesMag);
	}
	
	
	private static final float scale_decay = 0.6f;
	private static final float min_scale = (float) (1/Math.sqrt(2));
											// -> Area2 = (1/2)*Area1	
	private static void getNumberDraw(
			double number, 
			List<DrawText> series_sigFig,
			List<DrawText> series_mag ) 
	{
		final String str_num = NumberStringConverter.toString(number);
		final int str_length = str_num.length();
		final ArrayList<String> list = new ArrayList<String>();
		
		int index_dec = str_num.indexOf(".");
		int index_E = str_num.indexOf("E", index_dec);
		if (index_E == -1) index_E = str_length;
		
		if (index_dec == -1) {
			// index.e. non-decimal number
			groupWholeNum(str_num.substring(0,index_E), list);
		}
		else {
			groupWholeNum(str_num.substring(0,index_dec), list);
			final int index = list.size();
			groupDecimalNum(str_num.substring(index_dec+1, index_E),
					list.subList(list.size(), list.size()) );
			list.set(index, "."+list.get(index));
		}
		getDrawNumText(series_sigFig, list);
		list.clear();
		if (index_E != str_length) {
			list.add("E");
			groupWholeNum(str_num.substring(index_E+1), list);
		}
		getDrawNumText(series_mag, list);
	}
	private static void getDrawNumText(
			List<DrawText> series, 
			List<String> list_num) 
	{
		final int length = list_num.size();
		int i;
		DrawText comp;
		for (i=0; i<length; ++i) {
			comp = new DrawText(list_num.get(i));
			comp.setScale((float)
					(min_scale + (1-min_scale)*Math.pow(scale_decay,i)) );
			series.add(comp);
		}
	}
	private static void groupWholeNum(String str, List<String> list) {
		final int length = str.length();
		
		final byte start = (byte) (str.startsWith("-") ? 1 : 0);
		
		int index = ((length-start)%3) + start;
		if (index > start)
			list.add(str.substring(start, index));
		index += 3;
		for (; index<=length; index+=3) {
			list.add(str.substring(index-3, index));
		}
		
		if (start == 1)
			list.set(0, "-" + list.get(0));
	}
	private static void groupDecimalNum(String str, List<String> list) {
		final int length = str.length();
		
		int index = length - ( 2 + ((length+1)%3) );
		list.add(0, str.substring(Math.max(0, index), length));
		index-=3;
		for (; index>=0; index-=3) {
			list.add(0, str.substring(index, index+3));
		}
	}
	
	// Constructors (private; only constructed if necessary from static function)
	private AlignNumberSegments(List<DrawText> seriesSigFig) {
		super(AlignAxisBase.HORIZONTAL, spacing, AlignAxisBase.EDGE_BOTTOM);
		comps.addAll(seriesSigFig);
	}
	private AlignNumberSegments(List<DrawText> seriesSigFig, List<DrawText> seriesMag) {
		super(AlignAxisBase.HORIZONTAL, spacing, AlignAxisBase.EDGE_BOTTOM);
		comps.add(new AlignSeriesBuilder(seriesSigFig)
				.aligned_edge(AlignAxisBase.EDGE_BOTTOM)
				.whitespace(spacing)
				.build() );
		comps.add(new AlignSeriesBuilder(seriesMag)
				.aligned_edge(AlignAxisBase.EDGE_TOP)
				.whitespace(spacing)
				.build() );
	}
	
	// Overrides for AlignAxisBase
	@Override
	protected void addCompsToSeries() {
		// Sets max top/bottom dims
		comps.get(0).getSize(rectf);
		min_girth_start = orient.getGirthStart(rectf);
		max_girth_end = orient.getGirthEnd(rectf);
		// Arrange components in series
		for (AlignForm comp : comps)
			addCompToSeries(comp, STRETCH_NONE);
	}
	@Override
	protected Iterable<AlignForm> iterComps() {return comps;}
	@Override
	protected Iterable<AlignForm> iterCompsWithLoc() {return comps;}
	
	// Blank Overrides (AlignNumber cannot contain leaves)
	@Override
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T> tree, int[] branch_indices, boolean[] pars_active) {}
	@Override
	public <T extends DrawAligned> AlignForm getFirstInSeries(
			boolean orientation, ListTree<T>.Navigator nav) {
		if (orientation != getOrientation()) return this;
		else return comps.get(0).getFirstInSeries(orientation,nav);
	}
	@Override
	public <T extends DrawAligned> AlignForm getLastInSeries(
			boolean orientation, ListTree<T>.Navigator nav) {
		if (orientation != getOrientation()) return this;
		else return comps.get(0).getLastInSeries(orientation,nav);
	}
}
