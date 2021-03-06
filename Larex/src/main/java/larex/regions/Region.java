package larex.regions;

import larex.helper.TypeConverter;

import java.awt.Color;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import larex.positions.Position;
import larex.positions.PriorityPosition;
import larex.regions.colors.RegionColor;
import larex.regions.colors.RegionColors;
import larex.regions.type.RegionType;

public class Region {

	private static final int ALPHA_STANDARD = 80;
	private static final int ALPHA_ACTIVE = 150;

	private RegionType type;
	private String pageXmlIdentifier;
	private int minSize;

	private ArrayList<Position> positions;

	private boolean isVisible;

	private int maxOccurances;
	private PriorityPosition priorityPosition;

	private RegionColor regionColor;
	private Color standardColor;
	private Color activeColor;

	private static Mat activeMat;

	public Region(RegionType type, int minSize, RegionColor color, int maxOccurances, PriorityPosition priorityPosition,
			ArrayList<Position> positions) {
		setType(type);
		setPageXmlIdentifier(type);
		setMinSize(minSize);
		setColor(color);
		setMaxOccurances(maxOccurances);
		setPriorityPosition(priorityPosition);

		if (positions == null) {
			initRegions();
			calcPositionRects();
		} else {
			setPositions(positions);
		}
	}

	public Region(String typeString, int minSize, String colorString, int maxOccurances, String priorityString,
			ArrayList<Position> positions) {
		this(TypeConverter.stringToType(typeString), minSize, RegionColors.getColorByName(colorString), maxOccurances,
				calcPriorityPosition(maxOccurances, priorityString), positions);
	}

	private static PriorityPosition calcPriorityPosition(int maxOccurances, String priorityString) {
		if (maxOccurances == 1) {
			if (priorityString.equals("top")) {
				return PriorityPosition.top;
			} else if (priorityString.equals("bottom")) {
				return PriorityPosition.bottom;
			} else if (priorityString.equals("left")) {
				return PriorityPosition.left;
			} else if (priorityString.equals("right")) {
				return PriorityPosition.right;
			}
		}
		return null;
	}

	public void calcPositionRects(Mat image) {
		setActiveMat(image);

		for (Position position : positions) {
			Rect rect = position.calcRect(image);
			position.updateRect(rect, activeMat);
		}
	}

	public void calcPositionRects() {
		if (activeMat != null) {
			calcPositionRects(activeMat);
		}
	}

	public void removeNonPermanentFixedPositions() {
		ArrayList<Position> nonPermanentFixedPositions = new ArrayList<Position>();

		for (Position position : positions) {
			if (position.isFixed() && !position.isPermanent()) {
				nonPermanentFixedPositions.add(position);
			}
		}

		positions.removeAll(nonPermanentFixedPositions);
	}

	public void initRegions() {
		ArrayList<Position> positions = new ArrayList<Position>();

		/*if (type.equals(RegionType.image)) {
			Position position = new Position(0, 0, 1, 1);
			positions.add(position);
		}else if (type.equals(RegionType.ignore)) {
			Position position = new Position(0, 0, 0, 0);
			positions.add(position);
		}else */if (type.equals(RegionType.paragraph)) {
			Position position = new Position(0, 0, 1, 1);
			positions.add(position);
		} else if (type.equals(RegionType.marginalia)) {
			Position leftPosition = new Position(0, 0, 0.25, 1);
			Position rightPosition = new Position(0.75, 0, 1, 1);
			positions.add(leftPosition);
			positions.add(rightPosition);
		} else if (type.equals(RegionType.page_number)) {
			Position topPosition = new Position(0, 0, 1, 0.2);
			positions.add(topPosition);
		} else if (type.equals(RegionType.header) || type.equals(RegionType.heading)) {
			Position bottomPosition = new Position(0, 0, 1, 0.2);
			positions.add(bottomPosition);
		} else if (type.equals(RegionType.footer) || type.equals(RegionType.footnote)
				|| type.equals(RegionType.footnote_continued)) {
			Position bottomPosition = new Position(0, 0.8, 1, 1);
			positions.add(bottomPosition);
		} else if (!type.equals(RegionType.ignore) && !type.equals(RegionType.image)) {
			Position defaultPosition = new Position(0.2, 0.2, 0.8, 0.8);
			positions.add(defaultPosition);
		}

		setPositions(positions);
	}

	public void setColor(RegionColor regionColor) {
		setRegionColor(regionColor);
		Color color = regionColor.getColor();
		setStandardColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), ALPHA_STANDARD));
		setActiveColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), ALPHA_ACTIVE));
	}

	public void addPosition(Position position) {
		positions.add(position);
		position.calcPercentages(activeMat);
		calcPositionRects();
	}

	public RegionColor getColor() {
		return regionColor;
	}

	public RegionType getType() {
		return type;
	}

	public void setType(RegionType type) {
		this.type = type;
	}

	public String getPageXmlIdentifier() {
		return pageXmlIdentifier;
	}

	public void setPageXmlIdentifier(String pageXmlIdentifier) {
		this.pageXmlIdentifier = pageXmlIdentifier;
	}

	public void setPageXmlIdentifier(RegionType type) {
		String pageXmlIdentifier = type.toString();
		setPageXmlIdentifier(pageXmlIdentifier);
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public ArrayList<Position> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public int getMaxOccurances() {
		return maxOccurances;
	}

	public void setMaxOccurances(int maxOccurances) {
		this.maxOccurances = maxOccurances;
	}

	public PriorityPosition getPriorityPosition() {
		return priorityPosition;
	}

	public void setPriorityPosition(PriorityPosition priorityPosition) {
		this.priorityPosition = priorityPosition;
	}

	public Color getStandardColor() {
		return standardColor;
	}

	public void setStandardColor(Color standardColor) {
		this.standardColor = standardColor;
	}

	public Color getActiveColor() {
		return activeColor;
	}

	public void setActiveColor(Color activeColor) {
		this.activeColor = activeColor;
	}

	public RegionColor getRegionColor() {
		return regionColor;
	}

	public void setRegionColor(RegionColor regionColor) {
		this.regionColor = regionColor;
	}

	public void addPositionRect(Rect rect) {
		Position position = new Position(rect, activeMat);
		positions.add(position);
	}

	public Mat getActiveMat() {
		return activeMat;
	}

	public void setActiveMat(Mat activeMat) {
		Region.activeMat = activeMat;
	}
}