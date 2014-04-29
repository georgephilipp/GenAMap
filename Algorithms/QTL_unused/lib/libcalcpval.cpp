//
// MATLAB Compiler: 4.8 (R2008a)
// Date: Wed May 27 16:22:59 2009
// Arguments: "-B" "macro_default" "-W" "cpplib:libcalcpval" "-T" "link:lib"
// "calcpval.m" 
//

#include <stdio.h>
#define EXPORTING_libcalcpval 1
#include "libcalcpval.h"
#ifdef __cplusplus
extern "C" {
#endif

extern mclComponentData __MCC_libcalcpval_component_data;

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
#ifndef LIB_libcalcpval_C_API 
#define LIB_libcalcpval_C_API /* No special import/export declaration */
#endif

LIB_libcalcpval_C_API 
bool MW_CALL_CONV libcalcpvalInitializeWithHandlers(
    mclOutputHandlerFcn error_handler,
    mclOutputHandlerFcn print_handler
)
{
  if (_mcr_inst != NULL)
    return true;
  if (!mclmcrInitialize())
    return false;
  if (!mclInitializeComponentInstanceWithEmbeddedCTF(&_mcr_inst,
                                                     &__MCC_libcalcpval_component_data,
                                                     true, NoObjectType,
                                                     LibTarget, error_handler,
                                                     print_handler, 66190, (void *)(libcalcpvalInitializeWithHandlers)))
    return false;
  return true;
}

LIB_libcalcpval_C_API 
bool MW_CALL_CONV libcalcpvalInitialize(void)
{
  return libcalcpvalInitializeWithHandlers(mclDefaultErrorHandler,
                                           mclDefaultPrintHandler);
}

LIB_libcalcpval_C_API 
void MW_CALL_CONV libcalcpvalTerminate(void)
{
  if (_mcr_inst != NULL)
    mclTerminateInstance(&_mcr_inst);
}

LIB_libcalcpval_C_API 
void MW_CALL_CONV libcalcpvalPrintStackTrace(void) 
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


LIB_libcalcpval_C_API 
bool MW_CALL_CONV mlxCalcpval(int nlhs, mxArray *plhs[],
                              int nrhs, mxArray *prhs[])
{
  return mclFeval(_mcr_inst, "calcpval", nlhs, plhs, nrhs, prhs);
}

LIB_libcalcpval_CPP_API 
void MW_CALL_CONV calcpval(int nargout, mwArray& pval, const mwArray& zval)
{
  mclcppMlfFeval(_mcr_inst, "calcpval", nargout, 1, 1, &pval, &zval);
}
