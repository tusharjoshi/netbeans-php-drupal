<?php

function nbdevel_drush_command() {
  $items = array();

  $items['nb-hook'] = array(
    'description' => 'List implementations of a given hook and explore source of specified one.',
    'aliases' => array('nbh'),
    'arguments' => array(
      'hook' => 'The name of the hook to explore.'
    ),
    'examples' => array(
      'drush nbh menu_alter' => 'List modules that implements hook_menu_alter.',
    ),
  );
  $items['nb-module'] = array(
    'description' => 'List info about a specific module',
    'aliases' => array('nbm'),
    'bootstrap' => DRUSH_BOOTSTRAP_DRUPAL_FULL,
    'arguments' => array(
      'module-name' => 'The module of which you want info',
    ),
    'examples' => array(
      'drush nbm ctools' => 'Show module information of ctool',
    ),
  );
  

  return $items;
}

/**
 * Command handler. Show hook implementations.
 */
function drush_nbdevel_nb_hook($hook) {
  // Get implementations in the .install files as well.
  include_once './includes/install.inc';
  drupal_load_updates();

  if ($hook_implementations = module_implements($hook)) {
	foreach ($hook_implementations as $impl){
		$output.=drush_nbdevel_fn_view($impl . "_$hook");
	}
	//return $output;
  }
  else {
    drush_log(dt('No implementations.'), 'ok');
  }
}

function drush_nbdevel_fn_view($function_name) {
  // Get implementations in the .install files as well.


  if (strpos($function_name, '::') === FALSE) {
    if (!function_exists($function_name)) {
      return drush_set_error(dt('Function not found'));
    }
    $reflect = new ReflectionFunction($function_name);
  }
  else {
    list($class, $method) = explode('::', $function_name);
    if (!method_exists($class, $method)) {
      return drush_set_error(dt('Method not found'));
    }
    $reflect = new ReflectionMethod($class, $method);
  }
  $path = $reflect->getFileName();
  
  $path = str_replace('\\','/',$path);
  $file = str_replace(DRUPAL_ROOT, "", $path);

  $func_info = array('!file' => $file, '!path' => $path, '!startline' => $reflect->getStartLine(), '!endline' => $reflect->getEndLine());
  //drush_print_pipe(dt("!file -line !startline", $func_info));
  drush_print_pipe($reflect->getFileName());
  drush_print(dt("<a href='file://!path:!startline' style='text-decoration:underline;color:#0000AA;'>!file:!startline</a>", $func_info));

  //_drush_devel_print_function($reflect->getFileName(), $reflect->getStartLine(), $reflect->getEndLine());
}

/**
 * Implements validate function for drush command.
 */
function drush_nbdevel_validate($alias) {
  drush_set_option('module-name', $alias);
}

/**
 * Implements custom drush command.
 */
function drush_nbdevel() {
  $dependency_list = 'None';
  $module = drush_get_option('module-name', NULL);
  $rsc = drush_db_select('system', 'filename', 'name=:name', array(':name' => $module));
  $filename = drush_db_result($rsc);
  $info_file = drupal_parse_info_file(dirname($filename) . '/' . $module . '.info');
  if (isset($info_file['dependencies'])) {
    $dependency_list = implode("\n", $info_file['dependencies']);
  }

  drush_print_table(array(
    array('Name', $info_file['name']),
    array('Description', $info_file['description']),
    array('Version', $info_file['version']),
    array('Module path', dirname($filename)),
    array('Dependencies', $dependency_list),
  ),TRUE);
}