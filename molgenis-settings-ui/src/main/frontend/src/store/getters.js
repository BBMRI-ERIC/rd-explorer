// @flow
import { EntityToStateMapper } from '@molgenis/molgenis-ui-form'

const getMappedFields = (state) => {
  return state.rawSettings && state.rawSettings.meta && EntityToStateMapper.generateFormFields(state.rawSettings.meta)
}

// Is needed in standard getters.js component according to JSLint
export default {
  getMappedFields
}
