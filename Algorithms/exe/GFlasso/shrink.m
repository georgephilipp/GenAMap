function B=shrink(A, c)
    
      if ~exist('c', 'var')
          c=1;
      end 
      
      B=A;
      B(B>c)=c;
      B(B<-c)=-c;
  
end