package com.web.communication;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import larex.regions.type.RegionType;

/**
 * Communication object for the gui to request an export of a specifit page.
 * Contains the export settings and the page number to export.
 * 
 */
public class ExportRequest {

	@JsonProperty("page")
	private int page;
	@JsonProperty("segmentsToIgnore")
	private List<String> segmentsToIgnore;
	@JsonProperty("changedTypes")
	private Map<String,RegionType> changedTypes;

	@JsonCreator
	public ExportRequest(@JsonProperty("page") int page, @JsonProperty("segmentsToIgnore") List<String> segmentsToIgnore,@JsonProperty("changedTypes") Map<String,RegionType> changedTypes) {
		this.page = page;
		this.segmentsToIgnore = segmentsToIgnore;
		this.changedTypes = changedTypes;
	}

	public int getPage() {
		return page;
	}

	public List<String> getSegmentsToIgnore() {
		return segmentsToIgnore;
	}
	
	public Map<String, RegionType> getChangedTypes() {
		return changedTypes;
	}
}
