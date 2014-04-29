//
// MATLAB Compiler: 4.8 (R2008a)
// Date: Fri May 22 16:27:20 2009
// Arguments: "-B" "macro_default" "-W" "cpplib:libzscore" "-T" "link:lib"
// "zscore.m" 
//

#include <stdio.h>
#define EXPORTING_libzscore 1
#include "libzscore.h"
#ifdef __cplusplus
extern "C" {
#endif

extern mclComponentData __MCC_libzscore_component_data;

#ifdef __cplusplus
}
#endif


static HMCRINSTANCE _mcr_inst = NULL;


#ifdef __cplusplus
extern "C" {
#endif

static int mclDefaultPrintHandler(const char *s)
{
  return mclWrite(1 /* stdout */, s, sizeof(char)*strlen(s));
}

#ifdef __cplusplus
} /* End extern "C" block */
#endif

#ifdef __cplusplus
extern "C" {
#endif

static int mclDefaultErrorHandler(const char *s)
{
  int written = 0;
  size_t len = 0;
  len = strlen(s);
  written = mclWrite(2 /* stderr */, s, sizeof(char)*len);
  if (len > 0 && s[ len-1 ] != '\n')
    written += mclWrite(2 /* stderr */, "\n", sizeof(char));
  return written;
}

#ifdef __cplusplus
} /* End extern "C" block */
#endif

/* This symbol is defined in shared libraries. Define it here
 * (to nothing) in case this isn't a shared library. 
 */
#ifndef LIB_libzscore_C_API 
#define LIB_libzscore_C_API /* No special import/export declaration */
#endif

LIB_libzscore_C_API 
bool MW_CALL_CONV libzscoreInitializeWithHandlers(
    mclOutputHandlerFcn error_handler,
    mclOutputHandlerFcn print_handler
)
{
  if (_mcr_inst != NULL)
    return true;
  if (!mclmcrInitialize())
    return false;
  if (!mclInitializeComponentInstanceWithEmbeddedCTF(&_mcr_inst,
                                                     &__MCC_libzscore_component_data,
                                                     true, NoObjectType,
                                                     LibTarget, error_handler,
                                                     print_handler, 71137, (void *)(libzscoreInitializeWithHandlers)))
    return false;
  return true;
}

LIB_libzscore_C_API 
bool MW_CALL_CONV libzscoreInitialize(void)
{
  return libzscoreInitializeWithHandlers(mclDefaultErrorHandler,
                                         mclDefaultPrintHandler);
}

LIB_libzscore_C_API 
void MW_CALL_CONV libzscoreTerminate(void)
{
  if (_mcr_inst != NULL)
    mclTerminateInstance(&_mcr_inst);
}

LIB_libzscore_C_API 
void MW_CALL_CONV libzscorePrintStackTrace(void) 
{
  char** stackTrace;
  int stackDepth = mclGetStackTrace(_mcr_inst, &stackTrace);
  int i;
  for(i=0; i<stackDepth; i++)
  {
    mclWrite(2 /* stderr */, stackTrace[i], sizeof(char)*strlen(stackTrace[i]));
    mclWrite(2 /* stderr */, "\n", sizeof(char)*strlen("\n"));
  }
  mclFreeStackTrace(&stackTrace, stackDepth);
}


LIB_libzscore_C_API 
bool MW_CALL_CONV mlxZscore(int nlhs, mxArray *plhs[],
                            int nrhs, mxArray *prhs[])
{
  return mclFeval(_mcr_inst, "zscore", nlhs, plhs, nrhs, prhs);
}

LIB_libzscore_CPP_API 
void MW_CALL_CONV zscore(int nargout, mwArray& pval, const mwArray& U
                         , const mwArray& mu, const mwArray& sigma)
{
  mclcppMlfFeval(_mcr_inst, "zscore", nargout, 1, 3, &pval, &U, &mu, &sigma);
}
