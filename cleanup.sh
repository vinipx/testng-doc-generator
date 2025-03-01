#!/bin/bash

# Remove temporary script files
rm -f generate-sample-docs.sh update-docs.sh update-gherkin-reports.sh fix-test-titles.sh

# Remove RunDocGenerator.class if it exists
rm -f RunDocGenerator.class

echo "Cleanup completed successfully!"
