import femto.Game;
import femto.State;
import femto.input.Button;

import femto.mode.HiRes16Color;
import stage.TitleStage;

import sprites.Loot;

/**
 * Summary scene displays the final after stage summary. 
 * This class also saves the player variables between Zones.
 */ 
class SummaryStage extends State {
    HiRes16Color screen;
    Loot loot;
    int accuracy, c, highScore, score, bonusScore = 0;
    void init(){
        screen = Globals.screen;
        screen.setTextColor(0);
        accuracy = Globals.getAccuracy();
        
        if(!Globals.endless) {
            c = Globals.saveManager.currency;
        
            if(accuracy >= 50 && accuracy < 75){
                c += (int)(c*1.5);
            }else if(accuracy >= 75 && accuracy < 90){
                c += c*2;
            }else if(accuracy >= 90){
                c += c*3;
            }
        }else{
            score = Globals.score;
            highScore = Globals.endlessSaveManager.highScore;
            
            if(accuracy >= 50 && accuracy < 75){
                bonusScore = 1000;
            }else if(accuracy >= 75 && accuracy < 90){
                bonusScore = 2000;
            }else if(accuracy >= 90 && accuracy < 99){
                bonusScore = 3000;
            }else if(accuracy > 99){//holy crap dude
                bonusScore = 6000;
            }
        }
        loot = new Loot();
        loot.play();
    }
    void update(){
        screen.clear(3);
        if(Button.C.justPressed()){
            Game.changeState(new TitleStage());
        }
        
        screen.setTextPosition(110-40, 0);
        screen.println("Summary");
        
        screen.setTextPosition(42, 18);
        
        screen.print(    "Accuracy: " + accuracy);
        if(Globals.endless){
            screen.setTextPosition(42, 18+9);
            screen.print("Bonus:    " + bonusScore);
            screen.setTextPosition(42, 18+18);
            screen.print("Score:    " + score);
            screen.setTextPosition(42, 18+27);
            screen.print("Total:    " + (bonusScore + score));
            
            
            if(score+bonusScore > highScore){
                screen.setTextPosition(33, 70);
                screen.print("!NEW HIGH SCORE!");
                screen.setTextPosition(80, 88);
                screen.print((score+bonusScore));
            }else{
                screen.println("\nHigh Score: " + highScore);
            }
        }else{
            if(accuracy >= 50 && accuracy < 75){
                screen.println("Bonus x1.5");
            }else if(accuracy >= 75 && accuracy < 90){
                screen.println("Bonus x2.0");
            }else if(accuracy >= 90){
                screen.println("Bonus x3.0");
            }
            screen.println(" x"+c);
            loot.draw(screen, 0, 36);
        }
        
        
        screen.flush();
    }
    void shutdown(){
        if(!Globals.endless){
            switch(Globals.ZONE){
                case 0: Globals.saveManager.firstZoneClear = true; break;
                case 1: Globals.saveManager.secondZoneClear = true; break;
                case 2: Globals.saveManager.thirdZoneClear = true; break;
                case 3: Globals.saveManager.fourthZoneClear = true; break;
            }
            
            Globals.saveManager.currency = c;
            Globals.saveManager.saveCookie();
        }else{
            //TODO: Save high score and display score
            Globals.endless = false;
            if(bonusScore + score > Globals.endlessSaveManager.highScore) Globals.endlessSaveManager.highScore = (score + bonusScore);
            Globals.score = 0;
            Globals.shield = 100;
            // Refreshing shield on endless ending, because why not ;) 
            Globals.endlessSaveManager.saveCookie();
        }
        
        screen = null;
    }
}