import entities.BlastObject;
import entities.VirusObject;
import femto.input.Button;
import femto.mode.HiRes16Color;

import sprites.Blast;
import managers.DebrisManager;

public class BlastManager {
    
    Blast blast;
    // refresh: 50 how quickly blasts refresh
    // rate:    1  how many blasts are active at a time
    int cooldown = 0, refresh, rate, charge = 0, chargeSpeed;
    BlastObject[] blasts;
    float swordX = 0, swordY = 0;
    
    public BlastManager(){
        blast = new Blast();
        blast.charge();
        blasts = new BlastObject[]{
            new BlastObject(),
            new BlastObject(),
            new BlastObject(),
            new BlastObject(),
            new BlastObject(),
            new BlastObject(),
            new BlastObject(),
            new BlastObject(),
            new BlastObject(),
            new BlastObject()
        };
        rate = Globals.saveManager.rate;
        refresh = Globals.saveManager.refresh;
        chargeSpeed = Globals.saveManager.charge;
        
        System.out.println("[I] - Blasts initialized");
    }
    
    void tutorial(int ra, int re){
        rate = ra;
        refresh = re;
    }
    
    void reset(){
        for(BlastObject b : blasts){
            b.blast.x = -10;
            b.blast.y = -10;
        }
        if(Globals.endless){
            rate = Globals.endlessSaveManager.rate;
            refresh = Globals.endlessSaveManager.refresh;
            chargeSpeed = Globals.endlessSaveManager.charge;
        }else{
            rate = Globals.saveManager.rate;
            refresh = Globals.saveManager.refresh;
            chargeSpeed = Globals.saveManager.charge;
        }
        System.out.println("[I] - chargeSpeed: " + chargeSpeed);
    }
    
    void update(float x, float y, int dir){
        
        if(cooldown > 0){
            cooldown--;
        }
        for(int i = 0; i < rate; i++){
            blasts[i].update();
        }
        
        if(Button.B.isPressed() && charge > 0){
            if(Button.Left.isPressed()) charge--;
            if(Button.Right.isPressed()) charge--;
            if(Button.Down.isPressed()) charge--;
            if(Button.Up.isPressed()) charge--;
            return;//No shooting while
        } 
        if(Button.A.isPressed() && cooldown == 0){
            cooldown = refresh;
            for(int i = 0; i < rate; i++){
                if(!blasts[i].draw){
                    switch(dir){
                        case 0: blasts[i].init(-2.0f, 0.0f, x, y, charge == 100);break;//left
                        case 1: blasts[i].init(0.0f, -2.0f, x, y, charge == 100);break;//up
                        case 2: blasts[i].init(2.0f, 0.0f, x, y, charge == 100);break;//right
                        case 3: blasts[i].init(0.0f, 2.0f, x, y, charge == 100);break;//down
                    }
                    Globals.shots++;
                    break;
                }
            }
            // always set charge to 0
            charge = 0;
        }
        if(charge < 100){
            charge+=chargeSpeed;
            if(charge > 100)charge = 100;
        } 
    }
    
    public void hitDebris(DebrisManager debris){
        for(BlastObject b : blasts){
            if(b.draw && debris.checkCollides(b.blast.x+1, b.blast.y+1, b.blast.width()-2, b.blast.height()-2)){
                b.hit();
            }
        }
    }
    
    public int hitEnemy(float ex, float ey, float er){
        for(BlastObject b : blasts){
            if(b.draw && Globals.boundingBox(b.getX()+1, b.getY()+1,6, ex, ey, er)){
                b.hit();
                Globals.hit++;
                return (b.charged ? Globals.saveManager.damage : 1);
            }
        }
        return 0;
    }
    
    void render(HiRes16Color screen){
        if(charge == 100)blast.draw(screen, 78, 8);
        screen.fillRect(8, 12, (int)(charge * 68 / 100), 2, 11);
        screen.drawHLine(8, 8, (int)(cooldown * 70 / refresh), 8);
        
        for(int i = 0; i < rate; i++){
            blasts[i].render(screen);
        }
    }

}

