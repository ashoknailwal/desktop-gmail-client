package utilClasses;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Ashok on 4/29/2017.
 */
public class TextDraw{
    String text;
    StackPane parent;
    Canvas canvas;
    GraphicsContext gc;
    public TextDraw(String text, StackPane parent){
        this.text = text.toUpperCase();
        this.parent = parent;
        canvas = new Canvas(50, 50);
        gc = canvas.getGraphicsContext2D();
    }

    private static List<String > MATERIAL = Arrays.asList(
            "#4fc3f7",
                "#81d4fa",
                "#40c4ff",
                "#5c6bc0",
                "#9575cd",
                "#ffb74d",
                "#ffca28",
                "#ffea00",
                "#43a047",
                "#c6ff00",
                "#ff4081",
                "#e53935",
                "#2979ff"
    );

   //private static List<String> MATERIAL = Arrays.asList(
    //       "#ffe57373",
    //      /* default*/  "#fff16364",
    //        "#fff06292",
    //        /* default*/"#fff58559",
    //        "#ffba68c8",
    //        /* default*/"fff9a43e",
    //        "#ff9575cd",
    //        /* default*/"#ffe4c62e",
    //        "#ff7986cb",
    //        /* default*/"#ff67bf74",
    //        "#ff64b5f6",
    //        /* default*/"#ff59a2be",
    //        "#ff4fc3f7",
    //        /* default*/"#ff2093cd",
    //        "#ff4dd0e1",
    //        /* default*/"#ffad62a7",
    //        "#ff4db6ac",
    //        "#ff81c784",
    //        "#ffaed581",
    //        "#ffff8a65",
    //        "#ffd4e157",
    //        "#ffffd54f",
    //        "#ffffb74d",
    //        "#ffa1887f",
    //        "#ff90a4ae",
    //        /* default*/"#ff805781"
    //);*/


    public void buildCircularTextImage(){
        gc.setFill(Color.web(MATERIAL.get(new Random().nextInt(13))));
        gc.fillOval(0,0,50,50);
        Label l = new Label(text);
        l.setAlignment(Pos.CENTER);
        l.setFont(new Font(28));
        l.setTextFill(Color.WHITE);
        parent.getChildren().setAll(canvas,l);
    }

}
