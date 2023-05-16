package lintfordpickle.mailtrain.renderers.editor;

import org.lwjgl.opengl.GL11;

import lintfordpickle.mailtrain.controllers.editor.EditorHashGridController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class EditorHashGridRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Editor HashGrid Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected EditorHashGridController mHashGridController;
	protected LineBatch mLineBatch;
	private boolean mRenderHashGrid;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean renderHashGrid() {
		return mRenderHashGrid;
	}

	public void renderHashGrid(boolean newValue) {
		mRenderHashGrid = newValue;
	}

	@Override
	public boolean isInitialized() {
		return mHashGridController != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorHashGridRenderer(RendererManager rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mLineBatch = new LineBatch();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();
		mHashGridController = (EditorHashGridController) lControllerManager.getControllerByNameRequired(EditorHashGridController.CONTROLLER_NAME, mEntityGroupUid);
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mLineBatch.loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mLineBatch.unloadResources();
	}

	@Override
	public void draw(LintfordCore core) {
		if (mRenderHashGrid) {
			drawSpatialHashGridGrid(core);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawSpatialHashGridGrid(LintfordCore core) {
		final var lHashGrid = mHashGridController.hashGrid();
		final var mBoundaryWidth = lHashGrid.boundaryWidth();
		final var mBoundaryHeight = lHashGrid.boundaryHeight();

		final int lHalfBW = mBoundaryWidth / 2;
		final int lHalfBH = mBoundaryHeight / 2;

		final int mNumTilesWide = lHashGrid.numTilesWide();
		final int mNumTilesHigh = lHashGrid.numTilesHigh();

		final int lTileSizeW = mBoundaryWidth / mNumTilesWide;
		final int lTileSizeH = mBoundaryHeight / mNumTilesHigh;

		final var lFontUnit = mRendererManager.uiTextFont();

		mLineBatch.lineType(GL11.GL_LINES);
		mLineBatch.begin(core.gameCamera());
		lFontUnit.begin(core.gameCamera());
		final var lCamZoom = core.gameCamera().getZoomFactor();
		final var lFontSize = 0.5f / lCamZoom;
		final var lFontSpacing = 7.f / lCamZoom;

		for (int xx = 0; xx < mNumTilesWide; xx++) {
			mLineBatch.draw(-lHalfBW + (xx * lTileSizeW), -lHalfBH, -lHalfBW + (xx * lTileSizeW), lHalfBH, -0.01f, 1f, 0f, 0f, .5f);

			for (int yy = 0; yy < mNumTilesHigh; yy++) {
				mLineBatch.draw(-lHalfBW, -lHalfBH + (yy * lTileSizeH), lHalfBW, -lHalfBH + (yy * lTileSizeH), -0.01f, 1f, 1f, 0f, 1.0f);

				final int lCellKey = lHashGrid.getCellKeyFromWorldPosition(-lHalfBW + (xx * lTileSizeW), -lHalfBH + (yy * lTileSizeH));
				lFontUnit.drawText(String.valueOf(lCellKey), -lHalfBW + (xx * lTileSizeW) + 2f, -lHalfBH + (yy * lTileSizeH) + 1f, -0.001f, lFontSize);

				final var lCellContents = lHashGrid.getCell(lCellKey);
				if (lCellContents != null && lCellContents.size() > 0) {
					final int lNumCellContent = lCellContents.size();
					for (int j = 0; j < lNumCellContent; j++) {
						final var entity = lCellContents.get(j);
						lFontUnit.drawText(String.valueOf(entity.uid), -lHalfBW + (xx * lTileSizeW) + 10f, -lHalfBH + (yy * lTileSizeH) + lFontSpacing + (j * lFontSpacing), -0.001f, lFontSize);
					}
				}
			}
		}

		mLineBatch.end();
		lFontUnit.end();
	}
}
