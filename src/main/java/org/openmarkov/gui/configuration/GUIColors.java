package org.openmarkov.gui.configuration;


import java.awt.Color;
import java.util.List;

public class GUIColors {
    
    private GUIColors() {
    }
    
    public static final class General {
        public static final GUIColor CORRECT = new GUIColor(Color.GREEN);
        public static final GUIColor WRONG = new GUIColor(Color.RED);
        public static final GUIColor TEXT = new GUIColor(Color.BLACK);
        
        public static final GUIColor ATTENTION = new GUIColor(new Color(212, 56, 56));
        public static final GUIColor TRANSPARENT = new GUIColor(new Color(0, 0, 0, 0));
    }
    
    public static final class Network {
        
        public static final GUIColor BACKGROUND = new GUIColor(Color.WHITE)
                .inDark(new Color(69, 72, 74));
        public static final GUIColor LINK = new GUIColor(Color.BLACK);
        public static final GUIColor ALWAYS_OBSERVED = new GUIColor(new Color(128, 0, 0));
        public static final GUIColor REVELATION_ARC_VARIABLE = new GUIColor(new Color(128, 0, 0));
        
        public static final class ChanceNode {
            public static final GUIColor BACKGROUND = new GUIColor(new Color(251, 249, 153));
            public static final GUIColor FOREGROUND = new GUIColor(Color.BLACK);
            public static final GUIColor TEXT = new GUIColor(Color.BLACK);
            
            public static final GUIColor BACKGROUND_ON_PRE_RESOLUTION_FINDING = new GUIColor(Color.GRAY);
            public static final GUIColor BACKGROUND_ON_POST_RESOLUTION_FINDING = new GUIColor(Color.LIGHT_GRAY);
        }
        
        public static final class DecisionNode {
            public static final GUIColor BACKGROUND = new GUIColor(new Color(207, 227, 253));
            public static final GUIColor FOREGROUND = new GUIColor(Color.BLACK);
            public static final GUIColor TEXT = new GUIColor(Color.BLACK);
            
            public static final GUIColor BACKGROUND_ON_POLICY = new GUIColor(new Color(112, 142, 184));
            public static final GUIColor BACKGROUND_ON_PRE_RESOLUTION_FINDING = new GUIColor(Color.GRAY);
            public static final GUIColor BACKGROUND_ON_POST_RESOLUTION_FINDING = new GUIColor(Color.LIGHT_GRAY);
            
        }
        
        public static final class UtilityNode {
            public static final GUIColor BACKGROUND = new GUIColor(new Color(208, 230, 178));
            public static final GUIColor FOREGROUND = new GUIColor(Color.BLACK);
            public static final GUIColor TEXT = new GUIColor(Color.BLACK);
        }
        
        public static final class LinkRestriction {
            public static final GUIColor INCOMPATIBILITY_BACKGROUND = new GUIColor(new Color(255, 88, 88));
            public static final GUIColor COMPATIBILITY_BACKGROUND = new GUIColor(new Color(174, 255, 174));
            
            public static final GUIColor INCOMPATIBILITY_FOREGROUND = new GUIColor(new Color(255, 255, 255));
            public static final GUIColor COMPATIBILITY_FOREGROUND = new GUIColor(new Color(0, 0, 0));
        }
    }
    
    public static final class DecisionTree {
        public static final GUIColor BACKGROUND = Network.BACKGROUND;
        public static final GUIColor WINDOW = new GUIColor(new Color(0, 0, 255));
    }
    
    public static final class Tables {
        
        public static final List<GUIColor> HEADER_FOREGROUND_COLORS = List.of(
                new GUIColor(new Color(124, 107, 33)),
                new GUIColor(new Color(128, 0, 64)),
                new GUIColor(new Color(10, 51, 188)),
                new GUIColor(new Color(107, 169, 52))
        );
        
        public static final GUIColor HEADER_BACKGROUND = new GUIColor(new Color(220, 220, 220));
        public static final GUIColor FROZEN_CELL_BACKGROUND = new GUIColor(Color.LIGHT_GRAY);
        public static final GUIColor FROZEN_CELL_FOREGROUND = new GUIColor(Color.BLACK);
        public static final GUIColor EDITABLE_CELL_BACKGROUND = new GUIColor(Color.WHITE);
        public static final GUIColor EDITABLE_CELL_FOREGROUND = new GUIColor(Color.BLACK);
        
        
        public static final class KeyTable {
            public static final GUIColor GRID_COLOR = new GUIColor(Color.DARK_GRAY).negativizeInDark();
            public static final GUIColor SELECTION_BACKGROUND_COLOR = new GUIColor(new Color(211, 211, 211)).negativizeInDark();
            public static final GUIColor SELECTION_FOREGROUND_COLOR = new GUIColor(Color.BLACK).negativizeInDark();
            public static final GUIColor BACKGROUND_COLOR = new GUIColor(new Color(230, 230, 250))
                    .inDark(new Color(61, 61, 68));
            
        }
        
        public static final class ValuesTable {
            public static final GUIColor GRID_COLOR = new GUIColor(Color.GRAY);
            public static final GUIColor UNCERTAINTY_BACKGROUND = new GUIColor(Color.WHITE);
            public static final GUIColor OPTIMAL_POLICY = new GUIColor(new Color(80, 220, 95));
        }
    }
    
    public static final class CostEffectiveness {
        public static final GUIColor SERIES_COLOR = new GUIColor(Color.RED);
        public static final GUIColor BACKGROUND = new GUIColor(Color.WHITE);
    }
    
    public static final class DevelopmentTools {
        public static final class EditHistory {
            public static final GUIColor EDIT_TO_REDO_BACKGROUND = General.CORRECT;
            public static final GUIColor EDIT_TO_UNDO_BACKGROUND = General.WRONG;
        }
    }
    
    public static final class Graphics {
        public static final GUIColor DEFAULT_BACKGROUND_COLOR = new GUIColor(Color.LIGHT_GRAY).inDark(Color.DARK_GRAY);
        public static final GUIColor DEFAULT_BOX_BORDER_COLOR = new GUIColor(Color.BLACK).inDark(Color.WHITE);
    }
    
    public static final class SplashScreen {
        
        public static final GUIColor PROGRESS_BAR_FOREGROUND = new GUIColor(new Color(10, 110, 230));
        public static final GUIColor PROGRESS_BAR_BACKGROUND = new GUIColor(new Color(255, 255, 255));
    }
    
    public static final class TemporalEvoluation {
        public static final GUIColor BACKGROUND = new GUIColor(Color.WHITE);
        public static final GUIColor DOMAIN = new GUIColor(Color.DARK_GRAY);
    }
    
    public static final class Inference {
        public static final GUIColor BOX_BACKGROUND = new GUIColor(Color.WHITE);
        public static final GUIColor BOX_FOREGROUND = new GUIColor(Color.BLACK);
        public static final GUIColor BOX_TEXT = new GUIColor(Color.BLACK);
        public static final GUIColor STATE_BAR_BORDER = new GUIColor(Color.BLACK);
        
        public record EvidenceCaseColor(GUIColor background, GUIColor foreground) {
        }
        
        public static final List<EvidenceCaseColor> EVIDENCE_CASES_COLORS = List.of(
                new EvidenceCaseColor(new GUIColor(Color.RED), new GUIColor(Color.WHITE)),
                new EvidenceCaseColor(new GUIColor(Color.BLUE), new GUIColor(Color.WHITE)),
                new EvidenceCaseColor(new GUIColor(new Color(0, 190, 0)), new GUIColor(Color.WHITE)),
                new EvidenceCaseColor(new GUIColor(Color.MAGENTA), new GUIColor(Color.BLACK)),
                new EvidenceCaseColor(new GUIColor(new Color(255, 153, 51)), new GUIColor(Color.BLACK))
        );
        
    }
    
    public static final class SensitivityAnalysis {
        public static final GUIColor TEXT = new GUIColor(Color.BLACK);
        public static final GUIColor POINT_PER_PARAMETER_BACKGROUND = new GUIColor(Color.WHITE);
        public static final GUIColor PLOT_BACKGROUND = new GUIColor(Color.BLUE);
        public static final GUIColor CHART_BACKGROUND = new GUIColor(Color.WHITE);
        
        public static final List<GUIColor> BAR_COLORS = List.of(new GUIColor(Color.RED),
                                                                new GUIColor(Color.BLUE),
                                                                new GUIColor(Color.GREEN),
                                                                new GUIColor(Color.YELLOW),
                                                                new GUIColor(Color.MAGENTA),
                                                                new GUIColor(Color.CYAN),
                                                                new GUIColor(Color.ORANGE),
                                                                new GUIColor(Color.PINK),
                                                                new GUIColor(Color.GRAY),
                                                                new GUIColor(Color.LIGHT_GRAY),
                                                                new GUIColor(Color.DARK_GRAY));
        
    }
    
    public static final class FastMenu {
        
        public static final GUIColor OPTION_BACKGROUND = new GUIColor(new Color(255, 255, 255));
        
        public static final class Radial {
            public static final GUIColor CIRCLE_BACKGROUND = new GUIColor(new Color(200, 219, 220, 50));
            public static final GUIColor CIRCLE_OUTLINE = new GUIColor(new Color(130, 178, 180, 140));
            public static final GUIColor CIRCLE_CENTER = new GUIColor(new Color(130, 176, 180, 160));
        }
    }
    
}
