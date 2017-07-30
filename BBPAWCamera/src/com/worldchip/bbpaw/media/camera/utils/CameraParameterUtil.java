package com.worldchip.bbpaw.media.camera.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

public class CameraParameterUtil {

    private CameraSizeComparator sizeComparator = new CameraSizeComparator();  
    private static CameraParameterUtil myCamPara = null; 
    
    private CameraParameterUtil(){  
          
    }  
    public static CameraParameterUtil getInstance(){  
        if(myCamPara == null){  
            myCamPara = new CameraParameterUtil();  
            return myCamPara;  
        }  
        else{  
            return myCamPara;  
        }  
    }  
      
    public  Size getPreviewSize(List<Camera.Size> list, int maxWidth){  
        Collections.sort(list, sizeComparator);  
          
        int i = 0;  
        for(Size s:list){  
        	Log.e("lee", " Supported Preview Sizes : "+s.width +" * " + s.height);
            if((s.width <= maxWidth)){  
                Log.e("lee", "set Preview Size :w = " + s.width + " h = " + s.height);  
                break;  
            }  
            i++;  
        }  
  
        return list.get(i);  
    }  
    public Size getPictureSize(List<Camera.Size> list, int maxWidth){  
        Collections.sort(list, sizeComparator);  
          
        int i = 0;  
        for(Size s:list){  
        	Log.e("lee", " Supported Picture Sizes : "+s.width +" * " + s.height);
            if((s.width <= maxWidth)){  
                Log.e("lee", "set Picture Sizes : w = " + s.width + " h = " + s.height);  
                break;  
            }  
            i++;  
        }  
  
        return list.get(i);  
    }  
      
    public boolean equalRate(Size s, float rate){  
        float r = (float)(s.width)/(float)(s.height);  
        if(Math.abs(r - rate) <= 0.2)  
        {  
            return true;  
        }  
        else{  
            return false;  
        }  
    }  
      
    public  class CameraSizeComparator implements Comparator<Camera.Size>{  
        //按升序排列  
        public int compare(Size lhs, Size rhs) {  
            // TODO Auto-generated method stub
        	Log.e("lee", " lhs.width == "+lhs.width +" rhs.width == "+rhs.width);
            if(lhs.width == rhs.width){  
            return 0;  
            }  
            else if(lhs.width > rhs.width){  
                return 1;  
            }  
            else{  
                return -1;  
            }  
        }  
          
    }  
}
