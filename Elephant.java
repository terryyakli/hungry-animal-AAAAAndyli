import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

//Vry cool!!!
/**
 * The player
 * 
 * @author Andy
 * @version December 2023
 */
public class Elephant extends Actor
{
    int x = 0;
    int y = 0;
    double hMovement = 0;
    int jumpHeight = 0;
    double gravityModifier = 0;
    int dashable = 0;
    int jumpIndex = 0;
    int fallIndex = 0; 
    int landingIndex = 0;
    boolean isGrounded = true;
    boolean peakJump = false;
    
    GreenfootSound elephantSound = new GreenfootSound("elephantcub.mp3");
    GreenfootImage[] idleRight = new GreenfootImage[8];
    GreenfootImage[] idleLeft = new GreenfootImage[8];
    GreenfootImage[] walkRight = new GreenfootImage[8];
    GreenfootImage[] walkLeft = new GreenfootImage[8];
    GreenfootImage[] jumpRight = new GreenfootImage[2];
    GreenfootImage[] jumpLeft = new GreenfootImage[2];
    GreenfootImage[] fallRight = new GreenfootImage[2];
    GreenfootImage[] fallLeft = new GreenfootImage[2];
    GreenfootImage[] landRight = new GreenfootImage[5];
    GreenfootImage[] landLeft = new GreenfootImage[5];
    
    
    SimpleTimer animationTimer = new SimpleTimer();
    
    //direction elephant is facing
    String facing = "right";
    /**
     * constructor creates image for animation
     */
    public Elephant()
    {
        for(int i = 0; i < 8; i++)
        {
            idleRight[i] = new GreenfootImage("images/elephant_idle/idle" + i + ".png");
            walkRight[i] = new GreenfootImage("images/elephant_walk/tile00" + i + ".png");
            idleRight[i].scale(75,75);
            walkRight[i].scale(75,75);
        }
        for(int i = 0; i < 8; i++)
        {
            idleLeft[i] = new GreenfootImage("images/elephant_idle/idle" + i + ".png");
            walkLeft[i] = new GreenfootImage("images/elephant_walk/tile00" + i + ".png");
            idleLeft[i].mirrorHorizontally();
            walkLeft[i].mirrorHorizontally();
            idleLeft[i].scale(75,75);
            walkLeft[i].scale(75,75);
        }
        for(int i = 0; i < 2; i++)
        {
            jumpRight[i] = new GreenfootImage("images/elephant_jump/tile00" + (i+1) + ".png");
            jumpRight[i].scale(80,80);
        }
        for(int i = 0; i < 2; i++)
        {
            jumpLeft[i] = new GreenfootImage("images/elephant_jump/tile00" + (i+1) + ".png");
            jumpLeft[i].mirrorHorizontally();
            jumpLeft[i].scale(80,80);
        }
        for(int i = 2; i < 4; i++)
        {
            fallRight[i-2] = new GreenfootImage("images/elephant_jump/fall/tile00" + (i+1) + ".png");
            fallRight[i-2].scale(80,80);
        }
        for(int i = 2; i < 4; i++)
        {
            fallLeft[i-2] = new GreenfootImage("images/elephant_jump/fall/tile00" + (i+1) + ".png");
            fallLeft[i-2].mirrorHorizontally();
            fallLeft[i-2].scale(80,80);
        }
        for(int i = 4; i < 9; i++)
        {
            landRight[i-4] = new GreenfootImage("images/elephant_jump/fall/landing/tile00" + (i+1) + ".png");
            landRight[i-4].scale(80,80);
        }
        for(int i = 4; i < 9; i++)
        {
            landLeft[i-4] = new GreenfootImage("images/elephant_jump/fall/landing/tile00" + (i+1) + ".png");
            landLeft[i-4].mirrorHorizontally();
            landLeft[i-4].scale(80,80);
        }
        animationTimer.mark();
        setImage(idleRight[0]);
    }
    
    /**
     * Movement code, responds to keyboard inputs.
     */
    public void act()
    {
        x = getX();
        y = getY();
        //movement
        if(Greenfoot.isKeyDown("left"))
        {
            hMovement -= 1.3;
            facing = "left";
        }
        if(Greenfoot.isKeyDown("right"))
        {
            hMovement += 1.3;
            facing = "right";
        }
        if(Greenfoot.isKeyDown("up"))
        {
            isGrounded = false;
            jump();
            gravityModifier-=0.5;
        }
        //jump code
        if((peakJump==true&&isGrounded == false))
        {
            gravityModifier+=0.9;
        }
        
        if(Greenfoot.isKeyDown("space"))
        {
            dash();
        }
        bounding();
        setLocation(x+(int)hMovement, y+jumpHeight);
        if(y< 300)
        {
            if(!Greenfoot.isKeyDown("up"))
            {
                peakJump = true;
                jumpHeight += gravityModifier;
            }
            if(peakJump)
            {
                jumpHeight = 0;
                jumpHeight += gravityModifier;
            }
        }
        if(getY() > 300)
        {
            gravityModifier = 0;
            jumpHeight = 0;
            setLocation(x, 300);
            isGrounded = true;
            peakJump = false;
        }
        if(hMovement < 0)
        {
            hMovement++;
        }
        else if (hMovement > 0)
        {
            hMovement--;
        }
        eat();
        dashable++;
        if(isGrounded)
        {
            jumpIndex = 0;
            animateElephant();
        }
        else
        {
            animateJump();  
        }

    }
    /**
     * If it is touching an apple, destroy the apple
     * Add one to your score.
     */
    public void eat()
    {
        if(isTouching(Apple.class))
        {
            removeTouching(Apple.class);
            MyWorld world = (MyWorld) getWorld();
            world.createApple();
            world.increaseScore();
            elephantSound.play();
        }
    }
    /**
     * Moves the elephant vertically, and controls how high the elephant can jump
     */
    public void jump()
    {
        if(peakJump == false)
        {
            jumpHeight = -16;
            jumpHeight -= gravityModifier;
        }
        if(getY() < 200&&isGrounded == false)
        {
            peakJump = true;
        }
    }
    /**
     * Increases speed for a short amount of time
     */
    public void dash()
    {
        if(dashable > 100)
        {
            if(y!=300)
            {
                jumpHeight = 0;
                gravityModifier = 0;
                peakJump = true;
                isGrounded = false;
            }
            if(facing == "left")
            {
                hMovement = -20;
            }
            else
            {
                hMovement = 20;
            }
            dashable = 0;
        }
    }
    
    /**
     * prevents elephant from moving beyond the screen.
     */
    public void bounding()
    {
        if(getX()>600)
        {
            x = 600;
            hMovement = 0;
        }
        else if(getX() < 0)
        {
            x = 0;
            hMovement = 0;
        }

    }
    
    int imageIndex = 0;
    /**
     * animates the elephant
     */
    public void animateElephant()
    {
        if(animationTimer.millisElapsed() < 100)
        {
            return;
        }
        animationTimer.mark();
        if(y == 300)
        {
            if(facing.equals("right"))
            {
                if(Greenfoot.isKeyDown("right"))
                {
                    setImage(walkRight[imageIndex]);
                }
                else
                {
                    setImage(idleRight[imageIndex]);
                }            
                imageIndex = (imageIndex + 1) % idleRight.length;
            }
            else
            {
                if(Greenfoot.isKeyDown("left"))
                {
                    setImage(walkLeft[imageIndex]);
                }
                else
                {
                    setImage(idleLeft[imageIndex]);
                }            
                imageIndex = (imageIndex + 1) % idleLeft.length;        
            }
        }
    }
    public void animateJump()
    {
        if(animationTimer.millisElapsed() < 100)
        {
            return;
        }
        animationTimer.mark();
        if(!isGrounded)
        {
            if(facing.equals("right"))
            {
                setImage(jumpRight[jumpIndex]);
                if(jumpIndex < 0)
                {
                    jumpIndex++;
                }
                fallIndex = 0;
            }
            else
            {
                setImage(jumpLeft[jumpIndex]);
                if(jumpIndex < 0)
                {
                    jumpIndex++;
                }
                fallIndex = 0;
            }
            if(y>200&&peakJump)
            {
                if(facing.equals("right"))
                {
                    setImage(landRight[landingIndex]);
                }
                else
                {
                    setImage(landLeft[landingIndex]);
                }            
                landingIndex = (landingIndex + 1) % landLeft.length;  
            }
            else if(peakJump)
            {
                if(facing.equals("right"))
                {
                    setImage(fallRight[fallIndex]);
                    if(fallIndex < 0)
                    {
                        jumpIndex++;
                    }
                }
                else
                {
                    setImage(fallLeft[jumpIndex]);
                    if(fallIndex < 0)
                    {
                        jumpIndex++;
                    }
                }
                landingIndex = 0;
            }
        }
    }
}
