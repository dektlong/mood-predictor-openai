allow_k8s_contexts('tap-iterate')

SOURCE_IMAGE = os.getenv("SOURCE_IMAGE", default='your-registry.io/project/openai-source')
LOCAL_PATH = os.getenv("LOCAL_PATH", default='.')
NAMESPACE = os.getenv("NAMESPACE", default='default')

k8s_custom_deploy(
    'openai',
    apply_cmd="tanzu apps workload apply -f config/workload.yaml --update-strategy replace --debug --live-update" +
               " --local-path " + LOCAL_PATH +
               " --source-image " + SOURCE_IMAGE +
               " --service-ref=\"openai=services.apps.tanzu.vmware.com/v1alpha1:ResourceClaim:openai-claim\"" +
               " --namespace " + NAMESPACE +
               " --yes --output yaml",
    delete_cmd="tanzu apps workload delete -f config/workload.yaml --namespace " + NAMESPACE + " --yes",
    deps=['pom.xml', './target/classes'],
    container_selector='workload',
    live_update=[
      sync('./target/classes', '/workspace/BOOT-INF/classes')
    ]
)

k8s_resource('openai', port_forwards=["8080:8080"],
            extra_pod_selectors=[{'carto.run/workload-name': 'openai', 'app.kubernetes.io/component': 'run'}])